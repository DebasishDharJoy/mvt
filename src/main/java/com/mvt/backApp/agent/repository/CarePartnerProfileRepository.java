package com.mvt.backApp.agent.repository;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarePartnerProfileRepository extends JpaRepository<CarePartnerProfile, UUID> {
    Optional<CarePartnerProfile> findByUser(User user);
    Optional<CarePartnerProfile> findByPartnerId(String partnerId);
    boolean existsByPartnerId(String partnerId);
    Page<CarePartnerProfile> findAllByApprovalStatusOrderByCreatedAtDesc(
            ApprovalStatus status,
            Pageable pageable
    );
}
