package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.CommentRequest;
import com.group.cover_hub.model.dto.response.CommentResponse;
import com.group.cover_hub.model.entity.Comment;
import com.group.cover_hub.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CrudLookupService lookup;

    public List<CommentResponse> getAll() {
        return commentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CommentResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public CommentResponse create(CommentRequest request) {
        Comment comment = new Comment();
        applyRequest(comment, request);
        comment.setCreatedAt(LocalDateTime.now());
        return toResponse(commentRepository.save(comment));
    }

    public CommentResponse update(Long id, CommentRequest request) {
        Comment comment = findById(id);
        applyRequest(comment, request);
        return toResponse(commentRepository.save(comment));
    }

    public void delete(Long id) {
        commentRepository.delete(findById(id));
    }

    private Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> lookup.notFound("Comment", id));
    }

    private void applyRequest(Comment comment, CommentRequest request) {
        comment.setText(request.getText());
        comment.setCover(lookup.cover(request.getCoverId()));
        comment.setUser(lookup.user(request.getUserId()));
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .coverId(comment.getCover().getId())
                .userId(comment.getUser().getId())
                .build();
    }
}
