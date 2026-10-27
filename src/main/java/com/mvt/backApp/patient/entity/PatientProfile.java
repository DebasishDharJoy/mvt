package com.mvt.backApp.patient.entity;
import com.mvt.backApp.agent.entity.CarePartnerProfile;import com.mvt.backApp.common.entity.BaseAuditEntity;import com.mvt.backApp.user.entity.User;import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Getter @Setter @Entity @Table(name="patient_profiles") public class PatientProfile extends BaseAuditEntity{
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private User user;
 @Column(name="patient_id",nullable=false,unique=true,updatable=false,length=32) private String patientId;
 @Column(name="country_code",nullable=false,length=2) private String countryCode;
 @Column(name="mobile_number",nullable=false,length=20) private String mobileNumber;
 @Column(name="whatsapp_same_as_mobile",nullable=false) private boolean whatsappSameAsMobile;
 @Column(name="whatsapp_number",length=20) private String whatsappNumber;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="referred_by_partner_id") private CarePartnerProfile referredBy;
 @Column(name="referred_at") private LocalDateTime referredAt;
}
