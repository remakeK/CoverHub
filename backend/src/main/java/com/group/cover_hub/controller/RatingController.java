package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.RatingRequest;
import com.group.cover_hub.model.dto.response.RatingResponse;
import com.group.cover_hub.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<RatingResponse> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public RatingResponse getById(@PathVariable Long id) {
        return ratingService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse create(@Valid @RequestBody RatingRequest request) {
        return ratingService.create(request);
    }

    @PutMapping("/{id}")
    public RatingResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request
    ) {
        return ratingService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ratingService.delete(id);
    }
}
