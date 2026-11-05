package com.group.cover_hub.service;

import com.group.cover_hub.model.entity.*;
import com.group.cover_hub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CrudLookupService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OriginalSongRepository originalSongRepository;
    private final CoverRepository coverRepository;

    public User user(Long id) {
        return userRepository.
                findById(id).
                orElseThrow(() -> notFound("User", id));
    }

    public Category category(Long id) {
        return categoryRepository.
                findById(id).
                orElseThrow(() -> notFound("Category", id));
    }

    public OriginalSong originalSong(Long id) {
        return originalSongRepository.
                findById(id).orElseThrow(() -> notFound("Original song", id));
    }

    public Cover cover(Long id) {
        return coverRepository.findById(id).orElseThrow(() -> notFound("Cover", id));
    }

    public ResponseStatusException notFound(String resource, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " with id " + id + " was not found");
    }
}
