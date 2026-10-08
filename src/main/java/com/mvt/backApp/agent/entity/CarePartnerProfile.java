package com.mvt.backApp.agent.entity;

import com.mvt.backApp.common.entity.BaseAuditEntity;
import com.mvt.backApp.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_partner_profiles")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarePartnerProfile extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(unique = true, nullable = false, updatable = false)
    private String partnerId; // e.g., AGT-90015

    private String companyName; // Often required for vendors
    private String country;
    private String tradeLicense;
    private String gstNumber;
    private String passportNumber;

    // --- NEW FIELD FOR FILE UPLOAD ---
    @Column(name = "verification_document_url", nullable = false)
    private String verificationDocumentUrl; // Path to PDF/JPG in OCI storage

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason; // Useful if Master Admin rejects the document
}

