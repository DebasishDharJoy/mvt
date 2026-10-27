package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
public class CarePartnerApplicationSummaryDto {
    String partnerId;
    String firstName;
    String lastName;
    String email;
    String companyName;
    String countryCode;
    ApprovalStatus approvalStatus;
    LocalDateTime requestedAt;
}
