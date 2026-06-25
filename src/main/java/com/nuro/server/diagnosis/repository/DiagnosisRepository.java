package com.nuro.server.diagnosis.repository;

import com.nuro.server.diagnosis.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    List<Diagnosis> findAllByUserId(Long userId);
}