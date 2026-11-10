package com.group.cover_hub.controller;

import com.group.cover_hub.media.HlsProcessingService;
import com.group.cover_hub.media.MinioStorageService;
import com.group.cover_hub.repository.CoverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@RestController
@RequestMapping("/api/covers")
@RequiredArgsConstructor
public class HlsController {
    private final MinioStorageService minioStorageService;
    private final CoverRepository coverRepository;
    private final HlsProcessingService hlsProcessingService;

    @GetMapping("/{coverId}/media/hls/{*objectPath}")
    public ResponseEntity<StreamingResponseBody> getHlsFile(
            @PathVariable Long coverId,
            @PathVariable String objectPath
    ) {
        if (!coverRepository.existsById(coverId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Кавер не найден"
            );
        }

        // Убираем начальный слеш, который может присутствовать
        // в значении catch-all переменной Spring
        String relativePath = objectPath.startsWith("/")
                ? objectPath.substring(1)
                : objectPath;

        // Не позволяем обращаться к объектам вне директории кавера
        if (relativePath.isBlank()
                || relativePath.contains("\\")
                || relativePath.startsWith("/")
                || relativePath.contains("../")
                || relativePath.equals("..")
                || relativePath.contains("/./")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некорректный путь к HLS-файлу"
            );
        }

        String objectKey = "hls/" + coverId + "/" + relativePath;

        String contentType = getContentType(relativePath);

        InputStream inputStream;

        try {
            inputStream = minioStorageService.downloadObject(objectKey);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Не удалось получить HLS-файл",
                    e
            );
        }

        StreamingResponseBody responseBody = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.noCache())
                .body(responseBody);
    }

    private String getContentType(String path) {
        String extension = path.substring(path.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);

        return switch (extension) {
            case "m3u8" -> "application/vnd.apple.mpegurl";
            case "ts" -> "video/mp2t";
            default -> throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Неподдерживаемый тип HLS-файла"
            );
        };
    }
}