package com.group.cover_hub.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverPerformerRequest {
    @NotNull(message = "ID кавера обязателен")
    private Long coverId;
    private Long userId;
    private String name;
}
