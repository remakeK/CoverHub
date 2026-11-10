package com.group.cover_hub.controller;

import com.group.cover_hub.media.HlsProcessingService;
import com.group.cover_hub.media.StorageService;
import com.group.cover_hub.model.enums.MediaType;
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
import java.util.Map;

@RestController
@RequestMapping("/api/covers")
@RequiredArgsConstructor
public class MediaController {
    private final StorageService storageService;
    private final HlsProcessingService hlsProcessingService;

    @PostMapping("/{coverId}/media")
    public ResponseEntity<Void> uploadMedia(
            @PathVariable Long coverId,
            @RequestParam(required = false) MultipartFile video,
            @RequestParam(required = false) MultipartFile audio,
            @RequestParam(required = false) MultipartFile image
    ) {
        uploadIfPresent(coverId, video, MediaType.VIDEO);
        uploadIfPresent(coverId, audio, MediaType.AUDIO);
        uploadIfPresent(coverId, image, MediaType.IMAGE);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{coverId}/media/VIDEO/process")
    public ResponseEntity<Map<String, String>> processVideo(
            @PathVariable Long coverId
    ) {
        try {
            String masterPlaylistKey = hlsProcessingService.processVideo(coverId);

            String playbackUrl = "/api/covers/" + coverId + "/media/hls/master.m3u8";

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
                    "Ошибка при обработке видео",
                    e
            );
        }
    }

    @GetMapping("/{coverId}/media/{mediaType}/download")
    public ResponseEntity<StreamingResponseBody> downloadMedia(
            @PathVariable Long coverId,
            @PathVariable MediaType mediaType
    ) {
        InputStream inputStream =
                storageService.download(coverId, mediaType);

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

    private void uploadIfPresent(
            Long coverId,
            MultipartFile file,
            MediaType mediaType
    ) {
        if (file != null && !file.isEmpty()) {
            storageService.upload(coverId, file, mediaType);
        }
    }
}
