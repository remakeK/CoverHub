package com.group.cover_hub.controller;

import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/covers")
@RequiredArgsConstructor
public class MediaController {
    private final StorageService storageService;

    @PostMapping("/{id}/media")
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