package com.nuro.server.diagnosis.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * S3 저장 구현 -> 운영 환경에서 사용
 */
@Component
@Profile("!local")
@RequiredArgsConstructor
public class S3ImageStorage implements ImageStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    private static final String DIR = "diagnosis";

    @Override
    public String store(byte[] bytes, MimeType mimeType) {
        String ext = "jpg";
        if (mimeType != null) {
            String t = mimeType.toString();
            if ("image/png".equals(t)) ext = "png";
            else if ("image/webp".equals(t)) ext = "webp";
        }

        String fileName = UUID.randomUUID() + "." + ext;
        String key = DIR + "/" + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(mimeType != null ? mimeType.toString() : "image/jpeg")
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (S3Exception e) {
            throw new RuntimeException("S3 이미지 저장 실패", e);
        }

        return key;
    }

    @Override
    public String generateGetUrl(String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30)) // 30분간 유효
                .getObjectRequest(getRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}