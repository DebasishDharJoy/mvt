package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class CarePartnerApplicationDetailDto {
    String partnerId;
    String title;
    String firstName;
    String middleName;
    String lastName;
    String email;
    String companyName;
    String country;
    String tradeLicense;
    String gstNumber;
    String passportNumber;
    boolean verificationDocumentPresent;
    ApprovalStatus approvalStatus;
    UUID approvedBy;
    LocalDateTime approvedAt;
    String rejectionReason;
    LocalDateTime requestedAt;
}
