package com.nuro.server.diagnosis.util;

import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

/**
 * 이미지 리사이즈 유틸
 */
@Component
public class ImageResizer {

    // 긴 변 기준 최대 픽셀
    public static final int MAX_LONG_EDGE_PX = 1024;

    /**
     * 리사이즈된 이미지 바이트를 반환한다.
     *
     * @param original 원본 이미지 바이트
     * @param mimeType 이미지 MIME 타입
     * @return 리사이즈된 바이트
     */
    public byte[] resize(byte[] original, MimeType mimeType) {
        // TODO: ImageIO 등으로 디코딩 → 비율 유지 축소 → 인코딩
        throw new UnsupportedOperationException("TODO: ImageResizer.resize 구현 필요");
    }
}