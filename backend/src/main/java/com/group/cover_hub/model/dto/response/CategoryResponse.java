package com.group.cover_hub.model.dto.response;

import com.group.cover_hub.model.enums.CategoryTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private CategoryTitle categoryTitle;
}
