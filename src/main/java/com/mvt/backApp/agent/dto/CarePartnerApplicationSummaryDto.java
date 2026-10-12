package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CarePartnerApplicationSummaryDto {
    String partnerId;
    String firstName;
    String middleName;
    String lastName;
    String email;
    String companyName;
    String country;
    ApprovalStatus approvalStatus;
    LocalDateTime requestedAt;
}
