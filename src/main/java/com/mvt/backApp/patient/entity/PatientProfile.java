package com.mvt.backApp.patient.entity;

import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.common.entity.BaseAuditEntity;
import com.mvt.backApp.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProfile extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false, updatable = false)
    private String patientId; // e.g., PAT-10024

    private String country;

    // --- ATTRIBUTION TRACKING ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_partner_id") // Links to CarePartnerProfile ID
    private CarePartnerProfile referredBy;

    // Privacy compliant identifier mapping
    private String nationalId; // Used for Indian nationals
    private String passportNumber; // Used for Internationals
}
