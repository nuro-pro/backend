package com.nuro.server.notification.service;

public final class PhoneNumberUtil {

    private PhoneNumberUtil() {}

    /** 숫자만 남긴다 (하이픈/공백 제거) */
    public static String normalize(String phone) {
        return phone == null ? "" : phone.replaceAll("\\D", "");
    }

    /** 한국 휴대폰 번호(010, 11자리) 형식인지 */
    public static boolean isValidKoreanMobile(String normalized) {
        return normalized != null
                && normalized.length() == 11
                && normalized.startsWith("010");
    }

    /**
     * 문자열을 지정한 byte 수 이내로 자른다. (한글 2byte, ASCII 1byte 기준)
     * 잘렸으면 … 을 붙인다. 멀티바이트 글자가 중간에 끊기지 않게 글자 단위로 센다.
     */
    public static String truncateByBytes(String s, int maxBytes) {
        if (s == null || s.isBlank()) return "";
        int bytes = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int charBytes = (c < 128) ? 1 : 2;
            if (bytes + charBytes > maxBytes) {
                sb.append("…");
                break;
            }
            sb.append(c);
            bytes += charBytes;
        }
        return sb.toString();
    }
}