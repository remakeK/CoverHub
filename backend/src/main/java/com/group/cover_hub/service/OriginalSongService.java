package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.OriginalSongRequest;
import com.group.cover_hub.model.dto.response.OriginalSongResponse;
import com.group.cover_hub.model.entity.OriginalSong;
import com.group.cover_hub.repository.OriginalSongRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OriginalSongService {
    private final OriginalSongRepository songRepository;
    private final CrudLookupService lookup;

    public List<OriginalSongResponse> getAll() {
        return songRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OriginalSongResponse getById(Long id) {
        return toResponse(lookup.originalSong(id));
    }

    public OriginalSongResponse create(OriginalSongRequest request) {
        OriginalSong song = new OriginalSong();
        song.setTitle(request.getTitle());
        return toResponse(songRepository.save(song));
    }

    public OriginalSongResponse update(Long id, OriginalSongRequest request) {
        OriginalSong song = lookup.originalSong(id);
        song.setTitle(request.getTitle());
        return toResponse(songRepository.save(song));
    }

    public void delete(Long id) {
        songRepository.delete(lookup.originalSong(id));
    }

    private OriginalSongResponse toResponse(OriginalSong song) {
        return OriginalSongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .build();
    }
}
