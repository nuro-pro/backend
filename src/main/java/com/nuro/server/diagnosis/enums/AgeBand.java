package com.nuro.server.diagnosis.enums;

import lombok.Getter;

import java.util.Map;

@Getter
public enum AgeBand {

    TEENS("10대", 0, 19, Map.of(
            "수분", 64, "주름", 80, "색소", 72, "모공", 54, "민감", 56, "유분", 48), 63),
    TWENTIES("20대", 20, 29, Map.of(
            "수분", 60, "주름", 72, "색소", 66, "모공", 56, "민감", 60, "유분", 54), 60),
    THIRTIES("30대", 30, 39, Map.of(
            "수분", 56, "주름", 64, "색소", 60, "모공", 58, "민감", 60, "유분", 58), 57),
    FORTIES("40대", 40, 49, Map.of(
            "수분", 52, "주름", 56, "색소", 54, "모공", 60, "민감", 58, "유분", 60), 54),
    FIFTIES("50대", 50, 59, Map.of(
            "수분", 48, "주름", 48, "색소", 50, "모공", 62, "민감", 56, "유분", 62), 51),
    SIXTIES("60대", 60, 69, Map.of(
            "수분", 44, "주름", 42, "색소", 46, "모공", 64, "민감", 54, "유분", 64), 48),
    SEVENTIES("70대", 70, 79, Map.of(
            "수분", 40, "주름", 38, "색소", 42, "모공", 66, "민감", 52, "유분", 66), 45),
    EIGHTIES("80대", 80, 89, Map.of(
            "수분", 37, "주름", 35, "색소", 40, "모공", 67, "민감", 51, "유분", 67), 43),
    NINETIES("90대", 90, Integer.MAX_VALUE, Map.of(
            "수분", 34, "주름", 32, "색소", 38, "모공", 68, "민감", 50, "유분", 68), 41);

    // skinAge를 알 수 없을 때 사용할 기본 구간
    private static final AgeBand DEFAULT_BAND = TWENTIES;

    // 매핑되지 않은 지표명에 대한 안전 기본값
    private static final int FALLBACK_PEER_SCORE = 50;

    private final String label;
    private final int lowInclusive;
    private final int highInclusive;
    private final Map<String, Integer> peerMetricScores;
    private final int peerTotalScore;

    AgeBand(String label, int lowInclusive, int highInclusive,
            Map<String, Integer> peerMetricScores, int peerTotalScore) {
        this.label = label;
        this.lowInclusive = lowInclusive;
        this.highInclusive = highInclusive;
        this.peerMetricScores = peerMetricScores;
        this.peerTotalScore = peerTotalScore;
    }

    public static AgeBand of(Integer skinAge) {
        if (skinAge == null) {
            return DEFAULT_BAND;
        }
        for (AgeBand band : values()) {
            if (skinAge >= band.lowInclusive && skinAge <= band.highInclusive) {
                return band;
            }
        }
        // skinAge가 음수 등으로 어떤 구간에도 안 들면 최소 구간으로
        return TEENS;
    }

    public int peerScoreOf(String metricName) {
        if (metricName == null) {
            return FALLBACK_PEER_SCORE;
        }
        return peerMetricScores.getOrDefault(metricName.trim(), FALLBACK_PEER_SCORE);
    }
}