package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.OriginalArtistRequest;
import com.group.cover_hub.model.dto.response.OriginalArtistResponse;
import com.group.cover_hub.model.entity.OriginalArtist;
import com.group.cover_hub.repository.OriginalArtistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OriginalArtistService {
    private final OriginalArtistRepository artistRepository;
    private final CrudLookupService lookup;

    public List<OriginalArtistResponse> getAll() {
        return artistRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OriginalArtistResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public OriginalArtistResponse create(OriginalArtistRequest request) {
        OriginalArtist artist = new OriginalArtist();
        applyRequest(artist, request);
        return toResponse(artistRepository.save(artist));
    }

    public OriginalArtistResponse update(Long id, OriginalArtistRequest request) {
        OriginalArtist artist = findById(id);
        applyRequest(artist, request);
        return toResponse(artistRepository.save(artist));
    }

    public void delete(Long id) {
        artistRepository.delete(findById(id));
    }

    private OriginalArtist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> lookup.notFound("Original artist", id));
    }

    private void applyRequest(OriginalArtist artist, OriginalArtistRequest request) {
        artist.setName(request.getName());
        artist.setOriginalSong(lookup.originalSong(request.getOriginalSongId()));
    }

    private OriginalArtistResponse toResponse(OriginalArtist artist) {
        return OriginalArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .originalSongId(artist.getOriginalSong().getId())
                .build();
    }
}
