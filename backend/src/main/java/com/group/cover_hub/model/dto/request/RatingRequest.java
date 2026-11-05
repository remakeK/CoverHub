package com.group.cover_hub.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    @NotNull(message = "Оценка обязательна")
    private Boolean rating;
    @NotNull(message = "ID кавера обязателен")
    private Long coverId;
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
}
