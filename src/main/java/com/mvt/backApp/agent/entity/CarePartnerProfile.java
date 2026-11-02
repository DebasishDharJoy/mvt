package com.mvt.backApp.agent.entity;
import com.mvt.backApp.common.entity.BaseAuditEntity;import com.mvt.backApp.user.entity.User;import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Getter @Setter @Entity @Table(name="care_partner_profiles") public class CarePartnerProfile extends BaseAuditEntity{
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private User user;
 @Column(name="partner_id",nullable=false,unique=true,updatable=false,length=32) private String partnerId;
 @Column(name="company_name",nullable=false,length=200) private String companyName;
 @Column(name="country_code",nullable=false,length=2) private String countryCode;
 @Column(name="mobile_number",nullable=false,length=20) private String mobileNumber;
 @Column(name="whatsapp_same_as_mobile",nullable=false) private boolean whatsappSameAsMobile;
 @Column(name="whatsapp_number",length=20) private String whatsappNumber;
 @Enumerated(EnumType.STRING) @Column(name="document_type",nullable=false,length=30) private IdentityDocumentType documentType;
 @Column(name="document_number",nullable=false,length=150) private String documentNumber;
 @Column(name="verification_document_url",nullable=false,length=1000) private String verificationDocumentUrl;
 @Enumerated(EnumType.STRING) @Column(name="approval_status",nullable=false,length=20) private ApprovalStatus approvalStatus=ApprovalStatus.PENDING;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="approved_by") private User approvedBy;
 @Column(name="approved_at") private LocalDateTime approvedAt;
 @Column(name="rejection_reason",length=1000) private String rejectionReason;
}
