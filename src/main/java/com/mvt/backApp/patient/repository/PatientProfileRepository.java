package com.mvt.backApp.patient.repository;

import com.mvt.backApp.patient.entity.PatientProfile;
import com.mvt.backApp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, UUID> {
    Optional<PatientProfile> findByUser(User user);
    Optional<PatientProfile> findByPatientId(String patientId);
    boolean existsByPatientId(String patientId);
}
