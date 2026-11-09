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
    public ResponseEntity<String> uploadMedia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("mediaType") MediaType mediaType
    ) {
        String storageKey = storageService.upload(file, mediaType);

        return ResponseEntity.ok(storageKey);
    }
}