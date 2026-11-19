package com.group.cover_hub.media;

import com.group.cover_hub.exception.ResourceNotFoundException;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.CoverStatus;
import com.group.cover_hub.model.enums.MediaStatus;
import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.model.validator.MediaValidator;
import com.group.cover_hub.repository.CoverRepository;
import com.group.cover_hub.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class HlsProcessingService {
    private final MediaAssetRepository mediaAssetRepository;
    private final CoverRepository coverRepository;
    private final MinioStorageService minioStorageService;
    private final FfmpegService ffmpegService;

    @Value("${app.media.temp-dir:/app/temp}")
    private String tempDirectory;

    public String processMedia(Long coverId, MediaType mediaType) throws IOException, InterruptedException {
        if (mediaType != MediaType.AUDIO && mediaType != MediaType.VIDEO) {
            throw new IllegalArgumentException(
                    "Unsupported media type for HLS processing: " + mediaType
            );
        }

        MediaAsset media = mediaAssetRepository
                .findByCover_IdAndMediaType(coverId, mediaType)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Media " + mediaType + " not found for cover " + coverId
                        )
                );

        Cover cover = coverRepository
                .findById(coverId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Кавер не найден"
                                )
                        );

        cover.setStatus(CoverStatus.PROCESSING);
        media.setMediaStatus(MediaStatus.PROCESSING);
        coverRepository.save(cover);
        mediaAssetRepository.save(media);

        String mediaTypeString = media.getMediaType().name().toLowerCase(Locale.ROOT);

        Path workDirectory = null;

        try {
            Path tempRoot = Paths.get(tempDirectory);
            Files.createDirectories(tempRoot);

            workDirectory = Files.createTempDirectory(
                    tempRoot,
                    "cover-" + mediaTypeString + "-" + coverId + "-"
            );

            Path hlsDirectory = workDirectory
                    .resolve("hls")
                    .resolve(mediaTypeString);

            String hlsCoverPrefix = "hls/"
                    + mediaTypeString
                    + "/"
                    + coverId;

            Path inputPath = workDirectory.resolve(
                    "input." + MediaValidator.getExtension(
                            media.getOriginalFileName()
                    )
            );

            try (InputStream inputStream =
                         minioStorageService.downloadObject(media.getObjectKey())) {
                Files.copy(
                        inputStream,
                        inputPath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            ffmpegService.convertToHls(
                    inputPath,
                    hlsDirectory,
                    mediaTypeString
            );

            Path masterPlaylist = hlsDirectory.resolve("master.m3u8");

            if (!Files.isRegularFile(masterPlaylist)) {
                throw new IOException("FFmpeg did not create master.m3u8");
            }

            cleanupHls(coverId, mediaType);
            uploadHlsFiles(hlsDirectory, hlsCoverPrefix);

            String masterKey = hlsCoverPrefix + "/master.m3u8";

            switch (mediaType){
                case AUDIO -> cover.setAudioHlsMasterObjectKey(masterKey);
                case VIDEO -> cover.setVideoHlsMasterObjectKey(masterKey);
            }

            media.setMediaStatus(MediaStatus.PROCESSING_COMPLETED);
            mediaAssetRepository.save(media);

            boolean allProcessed =
                    mediaAssetRepository.existsByCover_Id(coverId)
                            && !mediaAssetRepository.existsByCover_IdAndMediaStatusNot(
                            coverId,
                            MediaStatus.PROCESSING_COMPLETED
                    );

            if(allProcessed){
                cover.setStatus(CoverStatus.PROCESSING_COMPLETED);
            }
            coverRepository.save(cover);
            return masterKey;

        } catch (IOException | RuntimeException e) {
            media.setMediaStatus(MediaStatus.PROCESSING_FAILED);
            mediaAssetRepository.save(media);
            cleanupHls(coverId, mediaType);

            throw e;

        } catch (InterruptedException e) {
            media.setMediaStatus(MediaStatus.PROCESSING_FAILED);
            mediaAssetRepository.save(media);
            Thread.currentThread().interrupt();
            cleanupHls(coverId, mediaType);

            throw e;

        } finally {
            deleteDirectory(workDirectory);
        }
    }

    private void uploadHlsFiles(Path hlsDirectory, String hlsPrefix) throws IOException {
        try (Stream<Path> paths = Files.walk(hlsDirectory)) {
            for (Path file : paths
                    .filter(Files::isRegularFile)
                    .filter(MediaValidator::isHlsFile)
                    .toList()) {

                String relativePath = hlsDirectory
                        .relativize(file)
                        .toString()
                        .replace('\\', '/');

                String objectKey = hlsPrefix + "/" + relativePath;

                long fileSize = Files.size(file);
                try (InputStream inputStream = Files.newInputStream(file)) {
                    minioStorageService.uploadObject(
                            objectKey,
                            inputStream,
                            fileSize,
                            getContentType(file)
                    );
                }
            }
        }
    }

    private String getContentType(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        }
        if (name.endsWith(".ts")) {
            return "video/mp2t";
        }
        return "application/octet-stream";
    }

    private void deleteDirectory(Path directory) {

        if (directory == null || !Files.exists(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(directory)) {

            for (Path path : paths
                    .sorted(Comparator.reverseOrder())
                    .toList()) {

                Files.deleteIfExists(path);
            }

        } catch (IOException e) {
            log.error(
                "Failed to delete temporary directory: {}", directory, e
        );
        }
    }

    private void cleanupHls(Long coverId, MediaType mediaType) {
        try {
            minioStorageService.deleteHls(coverId, mediaType);
        } catch (Exception e) {
            log.error(
                    "Failed to delete HLS files for cover {} and type {}",
                    coverId,
                    mediaType,
                    e
            );
        }
    }
}
