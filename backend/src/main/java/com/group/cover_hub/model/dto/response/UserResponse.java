package com.group.cover_hub.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String photoLink;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
