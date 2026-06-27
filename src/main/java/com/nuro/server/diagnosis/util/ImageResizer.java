package com.nuro.server.diagnosis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 이미지 리사이즈 유틸
 * 비전 LLM 입력 토큰 비용을 통제하기 위해 전송 전 긴 변을 줄인다
 */
@Slf4j
@Component
public class ImageResizer {

    // 긴 변 기준 최대 픽셀
    public static final int MAX_LONG_EDGE_PX = 1024;

    public byte[] resize(byte[] original, MimeType mimeType) {
        if (original == null || original.length == 0) {
            return original;
        }
        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(original));
            if (src == null) {
                // 디코딩 불가 포맷 → 원본 유지
                return original;
            }

            int width = src.getWidth();
            int height = src.getHeight();
            int longEdge = Math.max(width, height);
            if (longEdge <= MAX_LONG_EDGE_PX) {
                // 이미 충분히 작음 → 재인코딩 없이 원본 유지
                return original;
            }

            double ratio = (double) MAX_LONG_EDGE_PX / longEdge;
            int newWidth = Math.max(1, (int) Math.round(width * ratio));
            int newHeight = Math.max(1, (int) Math.round(height * ratio));

            String formatName = resolveFormat(mimeType);
            int imageType = "png".equals(formatName) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

            BufferedImage scaled = new BufferedImage(newWidth, newHeight, imageType);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean written = ImageIO.write(scaled, formatName, out);
            if (!written) {
                return original;
            }
            return out.toByteArray();
        } catch (IOException | RuntimeException e) {
            log.warn("이미지 리사이즈 실패, 원본으로 진행합니다: {}", e.getMessage());
            return original;
        }
    }

    private String resolveFormat(MimeType mimeType) {
        if (mimeType != null && "image/png".equals(mimeType.toString())) {
            return "png";
        }
        return "jpg";
    }
}