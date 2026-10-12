package com.mvt.backApp.agent.entity;

import com.mvt.backApp.common.entity.BaseAuditEntity;
import com.mvt.backApp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "care_partner_profiles")
public class CarePartnerProfile extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "partner_id", nullable = false, unique = true, updatable = false, length = 32)
    private String partnerId;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "trade_license", length = 150)
    private String tradeLicense;

    @Column(name = "gst_number", length = 100)
    private String gstNumber;

    @Column(name = "passport_number", length = 100)
    private String passportNumber;

    @Column(name = "verification_document_url", nullable = false, length = 1000)
    private String verificationDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
}
