package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.CoverRequest;
import com.group.cover_hub.model.dto.response.CoverResponse;
import com.group.cover_hub.service.CoverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/covers")
@RequiredArgsConstructor
public class CoverController {
    private final CoverService coverService;

    @GetMapping
    public List<CoverResponse> getAll() {
        return coverService.getAll();
    }

    @GetMapping("/{id}")
    public CoverResponse getById(@PathVariable Long id) {
        return coverService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoverResponse create(@Valid @RequestBody CoverRequest request) {
        return coverService.create(request);
    }

    @PutMapping("/{id}")
    public CoverResponse update(@PathVariable Long id, @Valid @RequestBody CoverRequest request) {
        return coverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        coverService.delete(id);
    }
}
