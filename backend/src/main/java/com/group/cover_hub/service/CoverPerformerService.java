package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.CoverPerformerRequest;
import com.group.cover_hub.model.dto.response.CoverPerformerResponse;
import com.group.cover_hub.model.entity.CoverPerformer;
import com.group.cover_hub.repository.CoverPerformerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoverPerformerService {
    private final CoverPerformerRepository performerRepository;
    private final CrudLookupService lookup;

    public List<CoverPerformerResponse> getAll() {
        return performerRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CoverPerformerResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public CoverPerformerResponse create(CoverPerformerRequest request) {
        CoverPerformer performer = new CoverPerformer();
        applyRequest(performer, request);
        return toResponse(performerRepository.save(performer));
    }

    public CoverPerformerResponse update(Long id, CoverPerformerRequest request) {
        CoverPerformer performer = findById(id);
        applyRequest(performer, request);
        return toResponse(performerRepository.save(performer));
    }

    public void delete(Long id) {
        performerRepository.delete(findById(id));
    }

    private CoverPerformer findById(Long id) {
        return performerRepository.findById(id)
                .orElseThrow(() -> lookup.notFound("Cover performer", id));
    }

    private void applyRequest(CoverPerformer performer, CoverPerformerRequest request) {
        performer.setName(request.getName());
        performer.setCover(lookup.cover(request.getCoverId()));
        performer.setUser(request.getUserId() == null ? null : lookup.user(request.getUserId()));
    }

    private CoverPerformerResponse toResponse(CoverPerformer performer) {
        return CoverPerformerResponse.builder()
                .id(performer.getId())
                .coverId(performer.getCover().getId())
                .userId(performer.getUser() == null ? null : performer.getUser().getId())
                .name(performer.getName())
                .build();
    }
}
