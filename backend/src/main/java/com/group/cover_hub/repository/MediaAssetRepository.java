package com.group.cover_hub.repository;

import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
    Optional<MediaAsset> findByCover_IdAndMediaType(
            Long coverId,
            MediaType mediaType
    );

    boolean existsByCover_IdAndMediaType(
            Long coverId,
            MediaType mediaType
    );
}
