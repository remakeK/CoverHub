package com.group.cover_hub.media;

import com.group.cover_hub.exception.ResourceNotFoundException;
import com.group.cover_hub.model.entity.Cover;
import com.group.cover_hub.model.entity.MediaAsset;
import com.group.cover_hub.model.enums.CoverStatus;
import com.group.cover_hub.model.enums.MediaType;
import com.group.cover_hub.repository.CoverRepository;
import com.group.cover_hub.repository.MediaAssetRepository;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MinioStorageService minioStorageService;
    private final MediaAssetRepository mediaAssetRepository;
    private final CoverRepository coverRepository;

    @Transactional
    public void deleteVideo(Long coverId) throws IOException, MinioException {
        Cover cover = coverRepository.findById(coverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Кавер не найден")
                );
        MediaAsset mediaAsset = mediaAssetRepository.findByCover_IdAndMediaType(coverId, MediaType.VIDEO)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Видео не найдено: ")
                );


        minioStorageService.deleteHls(coverId);
        minioStorageService.deleteObject(mediaAsset.getObjectKey());

        cover.getMediaAssets().remove(mediaAsset);
        mediaAssetRepository.delete(mediaAsset);
        cover.setHlsMasterObjectKey(null);

        if (!mediaAssetRepository.existsByCover_Id(coverId) && cover.getYoutubeUrl().isBlank()) {
            cover.setStatus(CoverStatus.DRAFT);
        }
    }
}
