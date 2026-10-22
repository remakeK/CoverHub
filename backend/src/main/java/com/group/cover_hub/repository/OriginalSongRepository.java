package com.group.cover_hub.repository;

import com.group.cover_hub.model.entity.OriginalSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalSongRepository extends JpaRepository<OriginalSong, Long> {
}
