package com.group.cover_hub.media;

import com.group.cover_hub.exception.ResourceNotFoundException;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.CoverStatus;
import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.repository.CoverRepository;
import com.group.cover_hub.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HlsProcessingService {
    private final MediaAssetRepository mediaAssetRepository;
    private final CoverRepository coverRepository;
    private final MinioStorageService minioStorageService;
    private final FfmpegService ffmpegService;

    @Value("${app.media.temp-dir:/app/temp}")
    private String tempDirectory;

    public String processVideo(Long coverId) throws IOException, InterruptedException {
        MediaAsset video = mediaAssetRepository
                .findByCover_IdAndMediaType(coverId, MediaType.VIDEO)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Video not found for cover: " + coverId
                        )
                );
        Cover cover = coverRepository
                .findById(coverId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Искомого кавера не существует."
                                )
                        );
        cover.setStatus(CoverStatus.PROCESSING);
        coverRepository.save(cover);

        Path tempRoot = Paths.get(tempDirectory);
        Files.createDirectories(tempRoot);
        Path workDirectory = Files.createTempDirectory(
                tempRoot,
                "cover-" + coverId + "-"
        );

        Path inputPath = workDirectory.resolve("input.mp4");
        Path hlsDirectory = workDirectory.resolve("hls");

        try {

            try (InputStream inputStream = minioStorageService.downloadObject(video.getObjectKey()))
            {
                Files.copy(inputStream, inputPath, StandardCopyOption.REPLACE_EXISTING);
            }

            ffmpegService.convertToHls(inputPath, hlsDirectory);
            Path masterPlaylist = hlsDirectory.resolve("master.m3u8");

            if (!Files.isRegularFile(masterPlaylist)) {
                throw new IOException(
                        "FFmpeg did not create master.m3u8"
                );
            }

            String hlsPrefix = "hls/" + coverId;
            uploadHlsFiles(hlsDirectory, hlsPrefix);
            String masterKey = hlsPrefix + "/master.m3u8";

            cover.setHlsMasterObjectKey(masterKey);
            cover.setStatus(CoverStatus.PROCESSING_COMPLETED);
            coverRepository.save(cover);

            return masterKey;

        } catch (IOException | RuntimeException e) {
            cover.setStatus(CoverStatus.PROCESSING_FAILED);
            coverRepository.save(cover);
            throw e;

        } catch (InterruptedException e) {
            cover.setStatus(CoverStatus.PROCESSING_FAILED);
            coverRepository.save(cover);
            Thread.currentThread().interrupt();
            throw e;

        } finally {
            deleteDirectory(workDirectory);
        }
    }


    private void uploadHlsFiles(Path hlsDirectory, String hlsPrefix) throws IOException {
        try (Stream<Path> paths = Files.walk(hlsDirectory)) {
            for (Path file : paths
                    .filter(Files::isRegularFile)
                    .filter(this::isHlsFile)
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

    private boolean isHlsFile(Path file) {
        String name = file.getFileName().toString();
        return name.endsWith(".m3u8") || name.endsWith(".ts");
    }

    private String getContentType(Path file) {
        String name = file.getFileName().toString();
        if (name.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        }
        if (name.endsWith(".ts")) {
            return "video/mp2t";
        }
        return "application/octet-stream";
    }

    private void deleteDirectory(Path directory) {

        if (!Files.exists(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(directory)) {

            for (Path path : paths
                    .sorted(Comparator.reverseOrder())
                    .toList()) {

                Files.deleteIfExists(path);
            }

        } catch (IOException e) {
            System.out.println("Ошибка при удалении временной деректории");;
        }
    }
}
