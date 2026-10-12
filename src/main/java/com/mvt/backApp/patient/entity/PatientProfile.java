package com.mvt.backApp.patient.entity;

import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.common.entity.BaseAuditEntity;
import com.mvt.backApp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patient_profiles")
public class PatientProfile extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "patient_id", nullable = false, unique = true, updatable = false, length = 32)
    private String patientId;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_partner_id")
    private CarePartnerProfile referredBy;

    @Column(name = "national_id", length = 100)
    private String nationalId;

    @Column(name = "passport_number", length = 100)
    private String passportNumber;
}
