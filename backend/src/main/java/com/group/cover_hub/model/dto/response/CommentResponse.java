package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long coverId;
    private Long userId;
}
