package com.nuro.server.user.repository;

import com.nuro.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByAgeBetween(Integer startAge, Integer endAge);
}