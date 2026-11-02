package com.mvt.backApp.user.entity;
import com.mvt.backApp.common.entity.BaseAuditEntity;
import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Getter @Setter @Entity @Table(name="email_verification_challenges")
public class EmailVerificationChallenge extends BaseAuditEntity {
 @Column(nullable=false,length=320) private String email;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private OtpPurpose purpose;
 @Column(name="otp_hash",nullable=false) private String otpHash;
 @Column(name="expires_at",nullable=false) private LocalDateTime expiresAt;
 @Column(name="attempt_count",nullable=false) private int attemptCount;
 @Column(name="consumed_at") private LocalDateTime consumedAt;
}
