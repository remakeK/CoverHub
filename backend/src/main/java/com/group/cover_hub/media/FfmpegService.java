package com.group.cover_hub.media;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class FfmpegService {
    public void convertToHls(Path inputPath, Path outputDirectory) throws IOException, InterruptedException {
        Files.createDirectories(outputDirectory);

        ProcessBuilder builder = new ProcessBuilder(
                "bash",
                "/app/scripts/convert-to-hls.sh",
                inputPath.toAbsolutePath().toString(),
                outputDirectory.toAbsolutePath().toString()
        );

        builder.redirectErrorStream(true);
        builder.redirectOutput(
                outputDirectory.resolve("ffmpeg.log").toFile()
        );

        Process process = builder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException(
                    "FFmpeg processing failed with code: " + exitCode
            );
        }
    }
}
