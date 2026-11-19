package com.group.cover_hub.controller;

import com.group.cover_hub.media.HlsProcessingService;
import com.group.cover_hub.media.MediaService;
import com.group.cover_hub.media.MinioStorageService;
import com.group.cover_hub.model.enums.MediaType;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/covers/media")
@RequiredArgsConstructor
public class MediaController {
    private final MinioStorageService storageService;
    private final HlsProcessingService hlsProcessingService;
    private final MediaService mediaService;

    @PostMapping("/{coverId}")
    public ResponseEntity<String> uploadMedia(
            @PathVariable Long coverId,
            @RequestParam(required = false) MultipartFile video,
            @RequestParam(required = false) MultipartFile audio,
            @RequestParam(required = false) MultipartFile image
    ) {
        uploadIfPresent(coverId, video, MediaType.VIDEO);
        uploadIfPresent(coverId, audio, MediaType.AUDIO);
        uploadIfPresent(coverId, image, MediaType.IMAGE);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Все файлы загрузились без ошибок.");
    }

    @PostMapping("/{coverId}/{mediaType}/process")
    public ResponseEntity<Map<String, String>> processMedia(
            @PathVariable Long coverId,
            @PathVariable String mediaType
    ) {

        try {
            hlsProcessingService.processMedia(coverId, fromStringToEnum(mediaType));

            String playbackUrl = "/api/covers/" + coverId + "/media/hls/" + mediaType + "/master.m3u8";

            return ResponseEntity.ok(
                    Map.of("playbackUrl", playbackUrl)
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Обработка видео была прервана",
                    e
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при обработке медиа",
                    e
            );
        }
    }

    @DeleteMapping("/{coverId}/{mediaType}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long coverId,
            @PathVariable String mediaType
    ) {
        try {
            mediaService.delete(coverId, fromStringToEnum(mediaType));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при удалении видео", e
            );
        } catch (MinioException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{coverId}//{mediaType}/download")
    public ResponseEntity<StreamingResponseBody> downloadMedia(
            @PathVariable Long coverId,
            @PathVariable String mediaType
    ) {
        InputStream inputStream = storageService.download(coverId, fromStringToEnum(mediaType));

        StreamingResponseBody responseBody = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"cover-" + coverId + "-" + mediaType + "\""
                )
                .body(responseBody);
    }

    private void uploadIfPresent(Long coverId, MultipartFile file, MediaType mediaType) {
        if (file != null && !file.isEmpty()) {
            storageService.upload(coverId, file, mediaType);
        }
    }
    private MediaType fromStringToEnum(String input) {
        try {
            return MediaType.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Указан неподдерживаемый тип медиа"
            );
        }
    }
}
