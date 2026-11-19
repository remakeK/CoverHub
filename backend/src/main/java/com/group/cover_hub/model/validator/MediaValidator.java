package com.group.cover_hub.model.validator;

import com.group.cover_hub.model.enums.SupportedAudioType;
import com.group.cover_hub.model.enums.SupportedImageType;
import com.group.cover_hub.model.enums.SupportedVideoType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Locale;

@Component
public class MediaValidator {
    public static boolean isSupportedAudioExtension(String extension) {
        try {
            SupportedAudioType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isSupportedAudioMime(String mimeType) {
        return mimeType.equals("audio/mpeg")
                || mimeType.equals("audio/wav")
                || mimeType.equals("audio/x-wav");
    }

    public static boolean isSupportedVideoExtension(String extension) {
        try {
            SupportedVideoType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isSupportedImageExtension(String extension) {
        try {
            SupportedImageType.valueOf(extension.substring(1));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isSupportedImageMime(String mimeType) {
        return mimeType.equals("image/png") || mimeType.equals("image/jpeg");
    }

    public static String getExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }

        int dotIndex = originalFilename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        }

        return originalFilename
                .substring(dotIndex)
                .toUpperCase(Locale.ROOT);
    }


    public static boolean isHlsFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".m3u8")
                || name.endsWith(".ts")
                || name.endsWith(".m4s")
                || name.endsWith(".mp4")
                || name.endsWith(".aac");
    }
}
