package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OriginalArtistResponse {
    private Long id;
    private String name;
    private Long originalSongId;
}
