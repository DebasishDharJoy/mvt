package com.mvt.backApp.user.repository;
import com.mvt.backApp.user.entity.*;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface EmailVerificationChallengeRepository extends JpaRepository<EmailVerificationChallenge,UUID>{
 Optional<EmailVerificationChallenge> findTopByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(String email,OtpPurpose purpose); }
