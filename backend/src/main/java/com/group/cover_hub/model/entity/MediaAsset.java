package com.group.cover_hub.model.entity;

import com.group.cover_hub.model.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="media_assets")
@Getter
@Setter
@NoArgsConstructor
public class MediaAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    private String storageKey;
    private String youtubeVideoId;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cover_id", nullable = false)
    private Cover cover;
}
