package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.OriginalSongRequest;
import com.group.cover_hub.model.dto.response.OriginalSongResponse;
import com.group.cover_hub.service.OriginalSongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/original-songs")
@RequiredArgsConstructor
public class OriginalSongController {
    private final OriginalSongService originalSongService;

    @GetMapping
    public List<OriginalSongResponse> getAll() {
        return originalSongService.getAll();
    }

    @GetMapping("/{id}")
    public OriginalSongResponse getById(@PathVariable Long id) {
        return originalSongService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OriginalSongResponse create(@Valid @RequestBody OriginalSongRequest request) {
        return originalSongService.create(request);
    }

    @PutMapping("/{id}")
    public OriginalSongResponse update(@PathVariable Long id, @Valid @RequestBody OriginalSongRequest request) {
        return originalSongService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        originalSongService.delete(id);
    }
}
