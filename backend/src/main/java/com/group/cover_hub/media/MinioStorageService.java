package com.group.cover_hub.media;

import com.group.cover_hub.exception.ResourceAlreadyExistsException;
import com.group.cover_hub.exception.ResourceNotFoundException;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.*;
import com.group.cover_hub.model.validator.MediaValidator;
import com.group.cover_hub.repository.CoverRepository;
import com.group.cover_hub.repository.MediaAssetRepository;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioStorageService {
    private final MinioClient minioClient;
    private final MediaAssetRepository mediaAssetRepository;
    private final CoverRepository coverRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Transactional
    public String upload(Long coverId, MultipartFile file, MediaType mediaType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Пустой файл");
        }

        String extension = MediaValidator.getExtension(file.getOriginalFilename());
        validate(file, mediaType, extension);

        String folder = switch (mediaType) {
            case AUDIO -> "audio";
            case VIDEO -> "video";
            case IMAGE -> "image";
        };

        String objectKey = folder + "/" + UUID.randomUUID() + extension;

        Cover cover = coverRepository.findById(coverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Кавер не найден: " + coverId)
                );

        if (mediaAssetRepository.existsByCover_IdAndMediaType(coverId, mediaType)) {
            throw new ResourceAlreadyExistsException("Для этого кавера уже загружен файл типа " + mediaType);
        }

        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setMediaType(mediaType);
        mediaAsset.setCover(cover);
        mediaAsset.setOriginalFileName(file.getOriginalFilename());
        mediaAsset.setMimeType(file.getContentType());
        mediaAsset.setFileSize(file.getSize());
        mediaAsset.setObjectKey(objectKey);
        mediaAsset.setMediaStatus(MediaStatus.DRAFT);


        try {
            uploadObject(
                    objectKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );
            if(mediaType.equals(MediaType.VIDEO) || mediaType.equals(MediaType.AUDIO)){
                cover.setStatus(CoverStatus.UPLOADED);
                mediaAsset.setMediaStatus(MediaStatus.UPLOADED);
                coverRepository.save(cover);
            }
            mediaAssetRepository.save(mediaAsset);

        } catch (IOException e) {
            try {
                delete(objectKey);
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Ошибка при удалении файла в MiniO"
                    );
            }

            throw new RuntimeException(
                    "Ошибка при загрузке файла в MinIO", e
            );
        }


        return objectKey;
    }

    public InputStream download(Long coverId, MediaType mediaType) {
        MediaAsset mediaAsset = mediaAssetRepository
                .findByCover_IdAndMediaType(coverId, mediaType)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Файл указанного типа не найден"
                        )
                );
        try {
            return downloadObject(mediaAsset.getObjectKey());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка при скачивании файла из MinIO", e
            );
        }
    }

    public void uploadObject(
            String objectKey,
            InputStream inputStream,
            long fileSize,
            String contentType
    ) throws IOException {

        try (inputStream) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, fileSize, (long) -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            if (e instanceof IOException ioException) {
                throw ioException;
            }

            throw new IOException(
                    "Ошибка при загрузке объекта в MinIO", e
            );
        }
    }

    public InputStream downloadObject(String objectKey) throws IOException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException(
                    "Ошибка при скачивании объекта из MinIO", e
            );
        }
    }
    public void deleteHls(Long coverId, MediaType mediaType) throws IOException {
        String mediaTypeString = mediaType.name().toLowerCase(Locale.ROOT);

        String hlsPrefix = "hls/"
                + mediaTypeString
                + "/"
                + coverId;

        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(hlsPrefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : objects) {
                String objectName = result.get().objectName();

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            }

        } catch (Exception e) {
            throw new IOException(
                    "Ошибка при удалении HLS-файлов кавера", e
            );
        }
    }

    public void delete(String objectKey) throws IOException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException(
                    "Ошибка при удалении объекта из MinIO", e
            );
        }
    }

    private void validate(
            MultipartFile file,
            MediaType mediaType,
            String extension
    ) {

        if (extension.isEmpty()) {
            throw new IllegalArgumentException(
                    "Файл не имеет расширения"
            );
        }

        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException(
                    "Не удалось определить MIME-тип файла: " + mimeType
            );
        }

        mimeType = mimeType.toLowerCase(Locale.ROOT);

        boolean valid = switch (mediaType) {
            case AUDIO ->
                    MediaValidator.isSupportedAudioExtension(extension)
                            && MediaValidator.isSupportedAudioMime(mimeType);

            case VIDEO ->
                    MediaValidator.isSupportedVideoExtension(extension)
                            && mimeType.equals("video/mp4");

            case IMAGE ->
                    MediaValidator.isSupportedImageExtension(extension)
                            && MediaValidator.isSupportedImageMime(mimeType);
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Недопустимое сочетание расширения и MIME-типа"
            );
        }
    }
}
