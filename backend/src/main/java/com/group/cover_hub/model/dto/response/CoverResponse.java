package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CoverResponse {
    private Long id;
    private String title;
    private String videoUrl;
    private String audioUrl;
    private String youtubeUrl;
    private LocalDateTime createdAt;
    private Long creatorId;
    private Long categoryId;
    private Long originalSongId;
}
