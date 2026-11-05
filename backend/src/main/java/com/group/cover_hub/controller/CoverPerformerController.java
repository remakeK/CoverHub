package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.CoverPerformerRequest;
import com.group.cover_hub.model.dto.response.CoverPerformerResponse;
import com.group.cover_hub.service.CoverPerformerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performers")
@RequiredArgsConstructor
public class CoverPerformerController {
    private final CoverPerformerService coverPerformerService;

    @GetMapping
    public List<CoverPerformerResponse> getAll() {
        return coverPerformerService.getAll();
    }

    @GetMapping("/{id}")
    public CoverPerformerResponse getById(@PathVariable Long id) {
        return coverPerformerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoverPerformerResponse create(@Valid @RequestBody CoverPerformerRequest request) {
        return coverPerformerService.create(request);
    }

    @PutMapping("/{id}")
    public CoverPerformerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CoverPerformerRequest request
    ) {
        return coverPerformerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        coverPerformerService.delete(id);
    }
}
