package com.nuro.server.diagnosis.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

/**
 * 로컬 디스크 저장 구현 -> 운영은 S3 구현으로 교체
 *
 * S3 연동 필요
 */
@Component
public class LocalImageStorage implements ImageStorage {

    @Override
    public String store(byte[] bytes, MimeType mimeType) {
        // TODO: 디렉토리 보장 → 파일명 생성 → 저장 → 경로/URL 반환
        throw new UnsupportedOperationException("TODO: LocalImageStorage.store 구현 필요");
    }
}