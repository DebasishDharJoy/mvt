package com.mvt.backApp.agent.service;

import com.mvt.backApp.agent.dto.CarePartnerApplicationDetailDto;
import com.mvt.backApp.agent.dto.CarePartnerApplicationSummaryDto;
import com.mvt.backApp.agent.dto.CarePartnerDecisionResponseDto;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarePartnerApprovalService {
    private final CarePartnerProfileRepository repository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CarePartnerApplicationSummaryDto> findApplications(
            ApprovalStatus status,
            Pageable pageable
    ) {
        return repository.findAllByApprovalStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public CarePartnerApplicationDetailDto findByPartnerId(String partnerId) {
        return toDetail(requiredProfile(partnerId));
    }

    @Transactional
    public CarePartnerDecisionResponseDto approve(String partnerId) {
        CarePartnerProfile profile = requiredPendingProfile(partnerId);
        LocalDateTime now = LocalDateTime.now();
        profile.setApprovalStatus(ApprovalStatus.APPROVED);
        profile.setApprovedBy(currentAdministrator());
        profile.setApprovedAt(now);
        profile.setRejectionReason(null);
        repository.save(profile);
        return decision(profile, now);
    }

    @Transactional
    public CarePartnerDecisionResponseDto reject(String partnerId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new ApiException(
                    "REJECTION_REASON_REQUIRED",
                    "validation.rejectionReason.required",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        CarePartnerProfile profile = requiredPendingProfile(partnerId);
        LocalDateTime now = LocalDateTime.now();
        profile.setApprovalStatus(ApprovalStatus.REJECTED);
        profile.setApprovedBy(currentAdministrator());
        profile.setApprovedAt(now);
        profile.setRejectionReason(reason.trim());
        repository.save(profile);
        return decision(profile, now);
    }

    private CarePartnerProfile requiredProfile(String partnerId) {
        return repository.findByPartnerId(partnerId)
                .orElseThrow(() -> new ApiException(
                        "CARE_PARTNER_APPLICATION_NOT_FOUND",
                        "error.carePartner.applicationNotFound",
                        HttpStatus.NOT_FOUND.value(),
                        partnerId
                ));
    }

    private CarePartnerProfile requiredPendingProfile(String partnerId) {
        CarePartnerProfile profile = requiredProfile(partnerId);
        if (profile.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ApiException(
                    "CARE_PARTNER_APPLICATION_ALREADY_REVIEWED",
                    "error.carePartner.applicationAlreadyReviewed",
                    HttpStatus.CONFLICT.value()
            );
        }
        return profile;
    }

    private User currentAdministrator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)
                || principal.getUserId() == null) {
            throw new ApiException(
                    "AUTHENTICATED_USER_REQUIRED",
                    "error.authentication.required",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }

        UUID administratorId = principal.getUserId();
        return userRepository.findById(administratorId)
                .orElseThrow(() -> new ApiException(
                        "AUTHENTICATED_USER_NOT_FOUND",
                        "error.authentication.required",
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    private CarePartnerApplicationSummaryDto toSummary(CarePartnerProfile profile) {
        return CarePartnerApplicationSummaryDto.builder()
                .partnerId(profile.getPartnerId())
                .firstName(profile.getUser().getFirstName())
                .middleName(profile.getUser().getMiddleName())
                .lastName(profile.getUser().getLastName())
                .email(profile.getUser().getEmail())
                .companyName(profile.getCompanyName())
                .country(profile.getCountry())
                .approvalStatus(profile.getApprovalStatus())
                .requestedAt(profile.getCreatedAt())
                .build();
    }

    private CarePartnerApplicationDetailDto toDetail(CarePartnerProfile profile) {
        return CarePartnerApplicationDetailDto.builder()
                .partnerId(profile.getPartnerId())
                .title(profile.getUser().getTitle())
                .firstName(profile.getUser().getFirstName())
                .middleName(profile.getUser().getMiddleName())
                .lastName(profile.getUser().getLastName())
                .email(profile.getUser().getEmail())
                .companyName(profile.getCompanyName())
                .country(profile.getCountry())
                .tradeLicense(profile.getTradeLicense())
                .gstNumber(profile.getGstNumber())
                .passportNumber(profile.getPassportNumber())
                .verificationDocumentPresent(profile.getVerificationDocumentUrl() != null)
                .approvalStatus(profile.getApprovalStatus())
                .approvedBy(profile.getApprovedBy() == null ? null : profile.getApprovedBy().getId())
                .approvedAt(profile.getApprovedAt())
                .rejectionReason(profile.getRejectionReason())
                .requestedAt(profile.getCreatedAt())
                .build();
    }

    private CarePartnerDecisionResponseDto decision(
            CarePartnerProfile profile,
            LocalDateTime reviewedAt
    ) {
        return CarePartnerDecisionResponseDto.builder()
                .partnerId(profile.getPartnerId())
                .approvalStatus(profile.getApprovalStatus())
                .reviewedAt(reviewedAt)
                .build();
    }
}
