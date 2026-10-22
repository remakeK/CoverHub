package com.group.cover_hub.repository;

import com.group.cover_hub.model.entity.CoverPerformer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverPerformerRepository extends JpaRepository<CoverPerformer, Long> {
}
