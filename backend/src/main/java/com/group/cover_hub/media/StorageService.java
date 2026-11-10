package com.group.cover_hub.media;

import com.group.cover_hub.model.enums.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


public interface StorageService {
    String upload(Long coverId, MultipartFile file, MediaType mediaType);
    InputStream download(Long coverId, MediaType mediaType);
    //void delete(String objectKey);
}