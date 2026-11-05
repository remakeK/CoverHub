package com.group.cover_hub.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverRequest {
    @NotBlank(message = "Название кавера обязательно")
    private String title;
    private String videoUrl;
    private String audioUrl;
    private String youtubeUrl;

    @NotNull(message = "ID автора обязателен")
    private Long creatorId;
    @NotNull(message = "ID категории обязателен")
    private Long categoryId;
    @NotNull(message = "ID оригинальной песни обязателен")
    private Long originalSongId;
}
