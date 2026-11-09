package com.group.cover_hub.controller;

import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@RestController
@RequestMapping("/api/covers")
@RequiredArgsConstructor
public class MediaController {
    private final StorageService storageService;

    @PostMapping("/{id}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadMedia(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile video,
            @RequestParam(required = false) MultipartFile audio,
            @RequestParam(required = false) MultipartFile image
    ) {
        System.out.println("VIDEO: " + (video != null ? video.getOriginalFilename() : "null"));
        System.out.println("AUDIO: " + (audio != null ? audio.getOriginalFilename() : "null"));
        System.out.println("IMAGE: " + (image != null ? image.getOriginalFilename() : "null"));

        uploadIfPresent(id, video, MediaType.VIDEO);
        uploadIfPresent(id, audio, MediaType.AUDIO);
        uploadIfPresent(id, image, MediaType.IMAGE);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{coverId}/media/{mediaType}/download")
    public ResponseEntity<StreamingResponseBody> downloadMedia(
            @PathVariable Long coverId,
            @PathVariable MediaType mediaType
    ) {
        InputStream inputStream = storageService.download(coverId, mediaType);

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
    ){
        if (file != null && !file.isEmpty()){
            String storageKey = storageService.upload(coverId, file, mediaType);
        }
    }
}