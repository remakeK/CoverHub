package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CoverPerformerResponse {
    private Long id;
    private Long coverId;
    private Long userId;
    private String name;
}
