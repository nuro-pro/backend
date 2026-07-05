package com.nuro.server.ingredient.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 스킨케어 성분 마스터 (나와 잘 맞는 성분)
 */
@Entity
@Getter
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String korName;

    @Column(nullable = false, length = 100)
    private String engName;

    @Column(nullable = false)
    private Integer ewgGrade;

    @Column(nullable = false, length = 20)
    private String riskLevel;

    @Column(nullable = false, length = 20)
    private String dataLevel;

    @Column(name = "\"desc\"", nullable = false, length = 500)
    private String desc;

    @ElementCollection
    @CollectionTable(name = "ingredient_effect", joinColumns = @JoinColumn(name = "ingredient_id"))
    @Column(name = "effect", nullable = false, length = 50)
    private List<String> effects = new ArrayList<>();

    @Column(nullable = false, length = 200)
    private String howToUse;

    @Column(nullable = false, length = 200)
    private String tip;

    private Ingredient(
            String korName,
            String engName,
            Integer ewgGrade,
            String riskLevel,
            String dataLevel,
            String desc,
            List<String> effects,
            String howToUse,
            String tip
    ) {
        this.korName = korName;
        this.engName = engName;
        this.ewgGrade = ewgGrade;
        this.riskLevel = riskLevel;
        this.dataLevel = dataLevel;
        this.desc = desc;
        this.effects = effects;
        this.howToUse = howToUse;
        this.tip = tip;
    }

    public static Ingredient create(
            String korName,
            String engName,
            Integer ewgGrade,
            String riskLevel,
            String dataLevel,
            String desc,
            List<String> effects,
            String howToUse,
            String tip
    ) {
        return new Ingredient(korName, engName, ewgGrade, riskLevel, dataLevel, desc, effects, howToUse, tip);
    }
}