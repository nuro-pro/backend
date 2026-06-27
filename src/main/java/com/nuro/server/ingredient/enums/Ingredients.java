package com.nuro.server.ingredient.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Ingredients {
    HYALURONIC_ACID(
            "히알루론산", "Hyaluronic acid",
            1, "낮음", "적당함",
            "높은 수분 결합력을 가진 보습 성분으로, 피부에 수분을 공급하고 오랫동안 유지해요. 피부를 매끄럽고 건강한 상태로 가꾸는 데 도움을 줘요.",
            List.of("깊은 보습", "탄력 케어", "진정 회복"),
            "세안 > 토너 > 히알루론산 > 보습제",
            "촉촉할 때 발라 수분을 끌어오고, 마지막에 보습제로 덮어 가둬주세요."
    ),

    GLYCERIN(
            "글리세린", "Glycerin",
            1, "낮음", "적당함",
            "대표적인 보습 성분으로 피부에 수분을 끌어당겨 촉촉함을 오래 유지하도록 도와줘요.",
            List.of("수분 공급", "보습 유지", "피부 보호"),
            "세안 > 토너 > 글리세린 > 보습제",
            "건조함이 느껴질 때 사용하여 피부 수분 밸런스를 유지해 주세요."
    ),

    CERAMIDE(
            "세라마이드", "Ceramide",
            1, "낮음", "적당함",
            "피부 장벽을 구성하는 주요 성분으로 수분 손실을 막고 피부 보호에 도움을 줘요.",
            List.of("장벽 강화", "수분 보호", "진정 회복"),
            "세안 > 토너 > 세라마이드 > 보습제",
            "건조한 피부에 사용하여 피부 장벽을 탄탄하게 관리해 주세요."
    ),

    NIACINAMIDE(
            "나이아신아마이드", "Nicotinic acid amide",
            1, "낮음", "적당함",
            "피부 톤 개선과 유분 밸런스 조절에 도움을 주며 건강한 피부 컨디션 유지에 도움을 줘요.",
            List.of("톤 개선", "유분 조절", "장벽 강화"),
            "세안 > 토너 > 나이아신아마이드 > 크림",
            "아침과 저녁 모두 사용 가능하며 피부를 균일하게 케어해 주세요."
    ),

    ZINC_PCA(
            "징크 PCA", "Zinc PCA",
            3, "보통", "확인 필요",
            "피지 조절에 도움을 주는 성분으로, 과도한 유분을 관리해 피부를 산뜻하게 유지해요. 유수분 밸런스를 맞추고 건강한 피부 컨디션 유지에 도움을 줘요.",
            List.of("피지 조절", "유분 균형", "모공 관리"),
            "세안 > 토너 > 징크 PCA > 크림",
            "유분이 고민되는 부위에 사용해 피지 밸런스를 관리하고 피부를 산뜻하게 유지해 주세요."
    ),

    BHA(
            "BHA", "Beta Hydroxy Acid",
            4, "보통", "적당함",
            "모공 속 피지와 노폐물을 관리하는 데 도움을 주며 피부를 깨끗하게 정돈해 줘요.",
            List.of("피지 조절", "모공 관리", "각질 제거"),
            "세안 > 토너 > BHA > 크림",
            "주 1~3회 저녁에 사용하고 피부 상태에 따라 횟수를 조절해 주세요."
    ),

    AHA(
            "AHA", "Alpha Hydroxy Acid",
            4, "보통", "적당함",
            "수용성 각질 케어 성분으로 피부 표면의 묵은 각질을 제거해 보다 매끄럽고 밝은 피부로 가꾸는 데 도움을 줘요.",
            List.of("각질 제거", "피부결 개선", "톤 개선"),
            "세안 > 토너 > AHA > 크림",
            "주 1~3회 저녁에 사용하고 낮에는 자외선 차단제를 사용해 주세요."
    ),

    LHA(
            "LHA", "Lipohydroxy Acid",
            4, "보통", "적당함",
            "지용성 각질 케어 성분으로 모공 속 노폐물과 각질을 부드럽게 관리하는 데 도움을 줘요.",
            List.of("모공 관리", "각질 제거", "피부결 개선"),
            "세안 > 토너 > LHA > 크림",
            "저녁에 사용하며 피부 상태에 따라 횟수를 조절해 주세요."
    ),

    PHA(
            "PHA", "Polyhydroxy Acid",
            2, "낮음", "적당함",
            "민감한 피부도 비교적 부담 없이 사용할 수 있는 순한 각질 케어 성분이에요.",
            List.of("순한 각질 제거", "수분 유지", "피부결 개선"),
            "세안 > 토너 > PHA > 크림",
            "주 2~3회 저녁에 사용하며 피부 상태를 확인해 주세요."
    ),

    RETINOL(
            "레티놀", "Retinol",
            3, "보통", "적당함",
            "비타민 A 유도체로 피부 결을 매끄럽게 정돈하고 탄력 개선과 주름 케어에 도움을 주는 성분이에요.",
            List.of("탄력 케어", "주름 개선", "피부결 정돈"),
            "세안 > 토너 > 레티놀 > 크림",
            "저녁에 소량 사용하며 피부 적응 후 사용 횟수를 늘려 주세요."
    ),

    PEPTIDE(
            "펩타이드", "Peptide",
            1, "낮음", "적당함",
            "피부 탄력과 건강한 피부 컨디션 유지에 도움을 주며 피부를 매끄럽게 가꾸어 줘요.",
            List.of("탄력 케어", "피부 강화", "주름 관리"),
            "세안 > 토너 > 펩타이드 > 크림",
            "매일 사용하여 탄력 있고 건강한 피부를 관리해 주세요."
    ),

    ADENOSINE(
            "아데노신", "Adenosine",
            1, "낮음", "적당함",
            "주름 개선 기능성 성분으로 피부 탄력 관리와 건강한 피부 유지에 도움을 줘요.",
            List.of("주름 개선", "탄력 케어", "피부 보호"),
            "세안 > 토너 > 아데노신 > 크림",
            "아침과 저녁 모두 사용 가능하며 꾸준히 관리해 주세요."
    ),

    VITAMIN_C(
            "비타민 C", "Vitamin C",
            2, "낮음", "적당함",
            "항산화 효과가 뛰어난 성분으로 피부를 밝고 생기 있게 가꾸는 데 도움을 줘요.",
            List.of("브라이트닝", "항산화", "톤 개선"),
            "세안 > 토너 > 비타민 C > 크림",
            "아침에 사용 후 자외선 차단제를 함께 발라 주세요."
    ),

    ALPHA_ARBUTIN(
            "알파 알부틴", "Alpha Arbutin",
            1, "낮음", "적당함",
            "피부를 맑고 균일하게 가꾸는 데 도움을 주며 칙칙한 피부 톤 개선에 도움을 줘요.",
            List.of("브라이트닝", "톤 개선", "색소 케어"),
            "세안 > 토너 > 알부틴 > 크림",
            "꾸준히 사용하여 맑고 깨끗한 피부 톤을 관리해 주세요."
    ),

    GLUTATHIONE(
            "글루타치온", "Glutathione",
            1, "낮음", "적당함",
            "강력한 항산화 성분으로 피부 톤을 맑고 균일하게 가꾸는 데 도움을 주며, 칙칙한 피부 개선에 도움을 줘요.",
            List.of("톤 개선", "항산화", "브라이트닝"),
            "세안 > 토너 > 글루타치온 > 크림",
            "피부 톤이 고민되는 부위에 사용하고 낮에는 자외선 차단제를 함께 사용해 주세요."
    ),

    PANTHENOL(
            "판테놀", "Panthenol",
            1, "낮음", "적당함",
            "비타민 B5 유도체로 피부 보습과 진정에 도움을 주며 손상된 피부 장벽 케어에 도움을 줘요.",
            List.of("진정 회복", "수분 공급", "장벽 강화"),
            "세안 > 토너 > 판테놀 > 크림",
            "건조하거나 민감한 피부에 사용하여 피부를 편안하게 관리해 주세요."
    ),

    CENTELLA(
            "병풀추출물", "Centella Asiatica",
            1, "낮음", "적당함",
            "피부 진정과 장벽 강화에 도움을 주는 성분으로, 외부 자극으로 민감해진 피부를 편안하게 케어하고 건강한 피부 상태를 유지하는 데 도움을 줘요.",
            List.of("피부 진정", "장벽 강화", "자극 완화"),
            "세안 > 토너 > 병풀추출물 > 크림",
            "민감하거나 붉어진 피부에 사용하여 피부를 편안하게 진정시켜 주세요."
    ),

    HOUTTUYNIA(
            "어성초", "Houttuynia Cordata",
            1, "낮음", "적당함",
            "피부 진정에 도움을 주는 식물성 성분으로 민감해진 피부를 편안하게 케어해 줘요.",
            List.of("피부 진정", "유분 케어", "장벽 보호"),
            "세안 > 토너 > 어성초 > 크림",
            "붉어지거나 예민한 피부에 사용하여 피부를 진정시켜 주세요."
    );

    private final String korName;       // 한글명
    private final String engName;       // 영문명
    private final int ewgGrade;         // EWG 등급
    private final String riskLevel;     // 위험도
    private final String dataLevel;     // 데이터
    private final String desc;          // 설명
    private final List<String> effects; // 효과 태그
    private final String howToUse;      // 사용 순서
    private final String tip;           // 사용 팁

    // 한글명으로 enum 찾기
    public static Ingredients findByKorName(String korName) {
        return Arrays.stream(values())
                .filter(i -> i.korName.equals(korName))
                .findFirst()
                .orElse(null);
    }

    // 한글명 또는 영문명으로 enum을 추출
    public static Ingredients findByName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = normalize(name);
        return Arrays.stream(values())
                .filter(i -> normalize(i.korName).equals(normalized)
                        || normalize(i.engName).equals(normalized))
                .findFirst()
                .orElse(null);
    }

    private static String normalize(String value) {
        return value.replaceAll("\\s+", "").toLowerCase();
    }
}