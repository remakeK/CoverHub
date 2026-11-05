package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.OriginalArtistRequest;
import com.group.cover_hub.model.dto.response.OriginalArtistResponse;
import com.group.cover_hub.service.OriginalArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/original-artists")
@RequiredArgsConstructor
public class OriginalArtistController {
    private final OriginalArtistService originalArtistService;

    @GetMapping
    public List<OriginalArtistResponse> getAll() {
        return originalArtistService.getAll();
    }

    @GetMapping("/{id}")
    public OriginalArtistResponse getById(@PathVariable Long id) {
        return originalArtistService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OriginalArtistResponse create(@Valid @RequestBody OriginalArtistRequest request) {
        return originalArtistService.create(request);
    }

    @PutMapping("/{id}")
    public OriginalArtistResponse update(@PathVariable Long id, @Valid @RequestBody OriginalArtistRequest request) {
        return originalArtistService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        originalArtistService.delete(id);
    }
}
