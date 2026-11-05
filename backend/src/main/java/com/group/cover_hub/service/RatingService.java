package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.RatingRequest;
import com.group.cover_hub.model.dto.response.RatingResponse;
import com.group.cover_hub.model.entity.Rating;
import com.group.cover_hub.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;
    private final CrudLookupService lookup;

    public List<RatingResponse> getAll() {
        return ratingRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RatingResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public RatingResponse create(RatingRequest request) {
        Rating rating = new Rating();
        applyRequest(rating, request);
        return toResponse(ratingRepository.save(rating));
    }

    public RatingResponse update(Long id, RatingRequest request) {
        Rating rating = findById(id);
        applyRequest(rating, request);
        return toResponse(ratingRepository.save(rating));
    }

    public void delete(Long id) {
        ratingRepository.delete(findById(id));
    }

    private Rating findById(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> lookup.notFound("Rating", id));
    }

    private void applyRequest(Rating rating, RatingRequest request) {
        rating.setRating(request.getRating());
        rating.setCover(lookup.cover(request.getCoverId()));
        rating.setUser(lookup.user(request.getUserId()));
    }

    private RatingResponse toResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rating(rating.isRating())
                .coverId(rating.getCover().getId())
                .userId(rating.getUser().getId())
                .build();
    }
}
