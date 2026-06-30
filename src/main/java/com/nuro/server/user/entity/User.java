package com.nuro.server.user.entity;

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
 * 익명 사용자(온보딩 단계에서 닉네임/나이만 입력)
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false)
    private Integer age;

    private User(String username, String password, String nickname, Integer age) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
    }

    public static User create(String username, String password, String nickname, Integer age) {
        // TODO: 닉네임/나이 도메인 불변식 검증
        return new User(username, password, nickname, age);
    }
}