package com.group.cover_hub.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}") String url;
    @Value("${minio.access-key}") String accessKey;
    @Value("${minio.secret-key}") String secretKey;

    @Bean
    public MinioClient minioClient(){

        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
