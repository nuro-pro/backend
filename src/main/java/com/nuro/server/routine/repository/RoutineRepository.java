package com.nuro.server.routine.repository;

import com.nuro.server.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    Optional<Routine> findByDiagnosisId(Long diagnosisId);
}