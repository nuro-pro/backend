package com.nuro.server.diagnosis.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AgeBand 단위 테스트")
class AgeBandTest {

    @Nested
    @DisplayName("of(skinAge) — 나이대 구간 매핑")
    class Of {

        @Test
        @DisplayName("성공 - 구간 내 나이는 해당 나이대로 매핑된다")
        void withinBand() {
            assertThat(AgeBand.of(24)).isEqualTo(AgeBand.TWENTIES);
            assertThat(AgeBand.of(45)).isEqualTo(AgeBand.FORTIES);
            assertThat(AgeBand.of(10)).isEqualTo(AgeBand.TEENS);
            assertThat(AgeBand.of(19)).isEqualTo(AgeBand.TEENS);
        }

        @Test
        @DisplayName("성공 - 90 이상은 90대 구간으로 매핑된다")
        void ninetiesAndAbove() {
            assertThat(AgeBand.of(90)).isEqualTo(AgeBand.NINETIES);
            assertThat(AgeBand.of(120)).isEqualTo(AgeBand.NINETIES);
        }

        @Test
        @DisplayName("성공 - skinAge가 null이면 기본 구간(20대)으로 폴백한다")
        void nullSkinAge() {
            assertThat(AgeBand.of(null)).isEqualTo(AgeBand.TWENTIES);
        }
    }

    @Nested
    @DisplayName("peer 평균 — 항상 non-null 보장")
    class PeerScores {

        @Test
        @DisplayName("성공 - 정의된 지표는 구간별 또래 평균을 반환한다")
        void knownMetric() {
            assertThat(AgeBand.TWENTIES.peerScoreOf("수분")).isEqualTo(60);
            assertThat(AgeBand.FORTIES.peerScoreOf("주름")).isEqualTo(56);
            assertThat(AgeBand.TWENTIES.getPeerTotalScore()).isEqualTo(60);
        }

        @Test
        @DisplayName("성공 - 미정의 지표명/null은 안전 기본값(50)을 반환한다")
        void unknownMetric() {
            assertThat(AgeBand.TWENTIES.peerScoreOf("없는지표")).isEqualTo(50);
            assertThat(AgeBand.TWENTIES.peerScoreOf(null)).isEqualTo(50);
        }
    }
}