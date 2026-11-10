package com.group.cover_hub.model.entity;

import com.group.cover_hub.model.enums.CoverStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "covers")
public class Cover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private String thumbnailUrl;

    @Column
    private String hlsMasterObjectKey;

    @Column
    private String audioUrl;

    @Column
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private CoverStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "original_song_id", nullable = false)
    private OriginalSong originalSong;

    @OneToMany(mappedBy = "cover")
    private List<CoverPerformer> coverPerformers = new ArrayList<>();

    @OneToMany(mappedBy = "cover")
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "cover")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "cover", cascade = CascadeType.ALL)
    private List<MediaAsset> mediaAssets = new ArrayList<>();
}
