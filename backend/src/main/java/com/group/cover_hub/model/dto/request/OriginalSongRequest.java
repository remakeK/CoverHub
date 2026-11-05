package com.group.cover_hub.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OriginalSongRequest {
    @NotBlank(message = "Название песни обязательно")
    private String title;
}
