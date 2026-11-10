package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.CoverRequest;
import com.group.cover_hub.model.dto.response.CoverResponse;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.repository.CoverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoverService {
    private final CoverRepository coverRepository;
    private final CrudLookupService lookup;

    public List<CoverResponse> getAll() {
        return coverRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CoverResponse getById(Long id) {
        return toResponse(lookup.cover(id));
    }

    public CoverResponse create(CoverRequest request) {
        Cover cover = new Cover();
        applyRequest(cover, request);
        cover.setCreatedAt(LocalDateTime.now());
        return toResponse(coverRepository.save(cover));
    }

    public CoverResponse update(Long id, CoverRequest request) {
        Cover cover = lookup.cover(id);
        applyRequest(cover, request);
        return toResponse(coverRepository.save(cover));
    }

    public void delete(Long id) {
        coverRepository.delete(lookup.cover(id));
    }

    private void applyRequest(Cover cover, CoverRequest request) {
        cover.setTitle(request.getTitle());
        cover.setAudioUrl(request.getAudioUrl());
        cover.setYoutubeUrl(request.getYoutubeUrl());
        cover.setUser(lookup.user(request.getCreatorId()));
        cover.setCategory(lookup.category(request.getCategoryId()));
        cover.setOriginalSong(lookup.originalSong(request.getOriginalSongId()));
    }

    private CoverResponse toResponse(Cover cover) {
        return CoverResponse.builder()
                .id(cover.getId())
                .title(cover.getTitle())
                .hlsMasterObjectKey(cover.getHlsMasterObjectKey())
                .audioUrl(cover.getAudioUrl())
                .youtubeUrl(cover.getYoutubeUrl())
                .createdAt(cover.getCreatedAt())
                .creatorId(cover.getUser().getId())
                .categoryId(cover.getCategory().getId())
                .originalSongId(cover.getOriginalSong().getId())
                .build();
    }
}
