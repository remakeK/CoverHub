package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RatingResponse {
    private Long id;
    private boolean rating;
    private Long coverId;
    private Long userId;
}
