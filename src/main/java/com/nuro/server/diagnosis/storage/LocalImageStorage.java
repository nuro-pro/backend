package com.nuro.server.diagnosis.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 로컬 디스크 저장 구현 -> 운영은 S3 구현으로 교체
 *
 * S3 연동 필요
 */
//@Component
public class LocalImageStorage implements ImageStorage {

    private static final String DIR = "uploads/diagnosis";

    @Override
    public String store(byte[] bytes, MimeType mimeType) {
        try {
            Files.createDirectories(Paths.get(DIR));

            String ext = "jpg";
            if (mimeType != null) {
                String t = mimeType.toString();
                if ("image/png".equals(t)) ext = "png";
                else if ("image/webp".equals(t)) ext = "webp";
            }

            String fileName = UUID.randomUUID() + "." + ext;
            Path path = Paths.get(DIR, fileName);
            Files.write(path, bytes);

            return "/uploads/diagnosis/" + fileName; // 저장된 파일 접근 경로
        } catch (IOException e) {
            throw new RuntimeException("로컬 이미지 저장 실패", e);
        }
    }
}