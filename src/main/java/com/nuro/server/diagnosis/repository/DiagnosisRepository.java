package com.nuro.server.diagnosis.repository;

import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.enums.DiagnosisStatus;
import org.aspectj.weaver.Iterators;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByUserIdInAndStatus(List<Long> userIds, DiagnosisStatus status);
    List<Diagnosis> findAllByUserId(Long userId);

    Optional<Diagnosis> findByShareId(String shareId);
}