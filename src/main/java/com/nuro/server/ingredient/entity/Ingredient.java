package com.nuro.server.ingredient.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String name;

    // 성분 효능/설명
    @Column(columnDefinition = "TEXT")
    private String description;

    private Ingredient(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Ingredient create(String name, String description) {
        return new Ingredient(name, description);
    }
}