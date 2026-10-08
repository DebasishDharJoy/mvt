package com.mvt.backApp.agent.repository;

import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarePartnerProfileRepository extends JpaRepository<CarePartnerProfile, Long> {
    Optional<CarePartnerProfile> findByUser(User user);
}