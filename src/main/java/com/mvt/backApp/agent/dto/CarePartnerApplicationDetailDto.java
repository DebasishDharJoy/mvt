package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class CarePartnerApplicationDetailDto {
    String partnerId;
    String firstName;
    String lastName;
    String email;
    String companyName;
    String countryCode;
    String mobileNumber;
    String whatsappNumber;
    IdentityDocumentType documentType;
    String documentNumber;
    boolean verificationDocumentPresent;
    ApprovalStatus approvalStatus;
    UUID approvedBy;
    LocalDateTime approvedAt;
    String rejectionReason;
    LocalDateTime requestedAt;
}
