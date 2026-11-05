package com.group.cover_hub.model.dto.request;

import com.group.cover_hub.model.enums.CategoryTitle;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotNull(message = "Категория обязательна")
    private CategoryTitle categoryTitle;
}
