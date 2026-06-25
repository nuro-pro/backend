package com.nuro.server.diagnosis.storage;

import org.springframework.util.MimeType;

/**
 * 이미지 저장 추상화
 * 로컬(디스크) ↔ 운영
 */
public interface ImageStorage {

    /**
     * 이미지를 저장하고 접근 가능한 위치(S3 key 또는 로컬 경로/URL)를 반환
     *
     * @param bytes    저장할 이미지 바이트(리사이즈 후)
     * @param mimeType 이미지 MIME 타입
     * @return 저장 위치 식별자
     */
    String store(byte[] bytes, MimeType mimeType);
}