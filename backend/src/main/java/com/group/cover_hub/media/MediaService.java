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
    public void delete(Long coverId, MediaType mediaType) throws IOException, MinioException {
        Cover cover = coverRepository.findById(coverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Кавер не найден")
                );
        MediaAsset mediaAsset = mediaAssetRepository.findByCover_IdAndMediaType(coverId, mediaType)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Медиа не найдено")
                );
        if(!mediaAsset.getMediaType().equals(MediaType.IMAGE)) {
            minioStorageService.deleteHls(coverId, mediaType);
        }
        minioStorageService.delete(mediaAsset.getObjectKey());

        cover.getMediaAssets().remove(mediaAsset);
        mediaAssetRepository.delete(mediaAsset);
        switch (mediaType){
            case VIDEO -> cover.setVideoHlsMasterObjectKey(null);
            case AUDIO -> cover.setAudioHlsMasterObjectKey(null);
            case IMAGE -> cover.setThumbnailUrl(null);
        }

        if (!mediaAssetRepository.existsByCover_Id(coverId) && cover.getYoutubeUrl() == null) {
            cover.setStatus(CoverStatus.DRAFT);
        }
    }
}
