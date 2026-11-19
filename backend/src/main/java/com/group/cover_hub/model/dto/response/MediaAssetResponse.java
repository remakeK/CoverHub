package com.group.cover_hub.model.dto.response;

import com.group.cover_hub.model.enums.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MediaAssetResponse {
    private Long id;
    private MediaType mediaType;
    private String objectKey;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
}
