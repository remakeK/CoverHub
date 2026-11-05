package com.group.cover_hub.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OriginalArtistRequest {
    @NotBlank(message = "Имя исполнителя обязательно")
    private String name;

    @NotNull(message = "ID оригинальной песни обязателен")
    private Long originalSongId;
}
