package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CarePartnerDecisionResponseDto {
    String partnerId;
    ApprovalStatus approvalStatus;
    LocalDateTime reviewedAt;
}
