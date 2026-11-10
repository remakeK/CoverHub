package com.group.cover_hub.media;

import com.group.cover_hub.exception.ResourceAlreadyExistsException;
import com.group.cover_hub.exception.ResourceNotFoundException;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.model.enums.SupportedAudioType;
import com.group.cover_hub.model.enums.SupportedImageType;
import com.group.cover_hub.model.enums.SupportedVideoType;
import com.group.cover_hub.repository.CoverRepository;
import com.group.cover_hub.repository.MediaAssetRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;
    private final MediaAssetRepository mediaAssetRepository;
    private final CoverRepository coverRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Transactional
    @Override
    public String upload(Long coverId, MultipartFile file, MediaType mediaType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Пустой файл");
        }

        String extension = getExtension(file.getOriginalFilename());
        validate(file, mediaType, extension);

        String folder = switch (mediaType) {
            case AUDIO -> "audio";
            case VIDEO -> "video";
            case IMAGE -> "image";
        };

        String objectKey = folder + "/" + UUID.randomUUID() + extension;

        Cover cover = coverRepository.findById(coverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Кавер не найден: " + coverId
                        )
                );

        if (mediaAssetRepository.existsByCover_IdAndMediaType(
                coverId, mediaType)) {

            throw new ResourceAlreadyExistsException(
                    "Для этого кавера уже загружен файл типа " + mediaType
            );
        }

        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setMediaType(mediaType);
        mediaAsset.setCover(cover);
        mediaAsset.setOriginalFileName(file.getOriginalFilename());
        mediaAsset.setMimeType(file.getContentType());
        mediaAsset.setFileSize(file.getSize());
        mediaAsset.setObjectKey(objectKey);

        mediaAssetRepository.save(mediaAsset);

        try {
            uploadObject(
                    objectKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка при загрузке файла в MinIO", e
            );
        }

        return objectKey;
    }

    @Override
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
                    "Ошибка при загрузке объекта в MinIO: " + objectKey, e
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
                    "Ошибка при скачивании объекта из MinIO: " + objectKey, e
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
                    "Не удалось определить MIME-тип файла"
            );
        }

        mimeType = mimeType.toLowerCase(Locale.ROOT);

        boolean valid = switch (mediaType) {
            case AUDIO ->
                    isSupportedAudioExtension(extension)
                            && isSupportedAudioMime(mimeType);

            case VIDEO ->
                    isSupportedVideoExtension(extension)
                            && mimeType.equals("video/mp4");

            case IMAGE ->
                    isSupportedImageExtension(extension)
                            && isSupportedImageMime(mimeType);
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Недопустимое сочетание расширения и MIME-типа"
            );
        }
    }

    private boolean isSupportedAudioExtension(String extension) {
        try {
            SupportedAudioType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isSupportedAudioMime(String mimeType) {
        return mimeType.equals("audio/mpeg")
                || mimeType.equals("audio/wav")
                || mimeType.equals("audio/x-wav");
    }

    private boolean isSupportedVideoExtension(String extension) {
        try {
            SupportedVideoType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isSupportedImageExtension(String extension) {
        try {
            SupportedImageType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isSupportedImageMime(String mimeType) {
        return mimeType.equals("image/png")
                || mimeType.equals("image/jpeg");
    }

    private String getExtension(String originalFilename) {

        if (originalFilename == null) {
            return "";
        }

        int dotIndex = originalFilename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        }

        return originalFilename
                .substring(dotIndex)
                .toUpperCase(Locale.ROOT);
    }
}
