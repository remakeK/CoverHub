package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.request.CommentRequest;
import com.group.cover_hub.model.dto.response.CommentResponse;
import com.group.cover_hub.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponse> getAll() {
        return commentService.getAll();
    }

    @GetMapping("/{id}")
    public CommentResponse getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@Valid @RequestBody CommentRequest request) {
        return commentService.create(request);
    }

    @PutMapping("/{id}")
    public CommentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ) {
        return commentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
