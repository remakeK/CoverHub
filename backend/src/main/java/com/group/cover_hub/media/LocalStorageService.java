package com.group.cover_hub.media;

import com.group.cover_hub.model.enums.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;


public class LocalStorageService implements StorageService {
    private final Path root = Paths.get("storage")
            .toAbsolutePath()
            .normalize();

    public String upload(Long coverId, MultipartFile file, MediaType mediaType) {
        try {
            if (file.isEmpty()) {throw new IllegalArgumentException("Файл пустой");}

            String directory = switch (mediaType) {
                case AUDIO -> "audio";
                case VIDEO -> "video";
                case IMAGE -> "image";
            };

            String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path destination = root.resolve(directory).resolve(filename);
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination);

            return directory + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    @Override
    public InputStream download(Long coverId, MediaType mediaType) {
        return null;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }

        return originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}