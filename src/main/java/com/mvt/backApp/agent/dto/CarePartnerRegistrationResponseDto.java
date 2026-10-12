package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CarePartnerRegistrationResponseDto {
    String partnerId;
    String email;
    ApprovalStatus approvalStatus;
}
