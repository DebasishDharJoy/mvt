package com.mvt.backApp.agent.controller;

import com.mvt.backApp.agent.dto.*;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.service.CarePartnerApprovalService;
import com.mvt.backApp.common.dto.ApiResponse;
import com.mvt.backApp.common.service.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/care-partner-applications")
@RequiredArgsConstructor
public class CarePartnerApprovalController {
    private final CarePartnerApprovalService service;
    private final ApiResponseFactory responses;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_VIEW)")
    public ResponseEntity<ApiResponse<Page<CarePartnerApplicationSummaryDto>>> list(@RequestParam(defaultValue = "PENDING") ApprovalStatus status, Pageable pageable) {
        return ResponseEntity.ok(responses.success("success.carePartner.applicationsFetched", 200, service.findApplications(status, pageable)));
    }

    @GetMapping("/{partnerId}")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_VIEW)")
    public ResponseEntity<ApiResponse<CarePartnerApplicationDetailDto>> detail(@PathVariable String partnerId) {
        return ResponseEntity.ok(responses.success("success.carePartner.applicationFetched", 200, service.findByPartnerId(partnerId)));
    }

    @PostMapping("/{partnerId}/approve")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_APPROVE)")
    public ResponseEntity<ApiResponse<CarePartnerDecisionResponseDto>> approve(@PathVariable String partnerId) {
        return ResponseEntity.ok(responses.success("success.carePartner.approved", 200, service.approve(partnerId)));
    }

    @PostMapping("/{partnerId}/reject")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_REJECT)")
    public ResponseEntity<ApiResponse<CarePartnerDecisionResponseDto>> reject(@PathVariable String partnerId, @Valid @RequestBody CarePartnerRejectionRequestDto r) {
        return ResponseEntity.ok(responses.success("success.carePartner.rejected", 200, service.reject(partnerId, r.getReason())));
    }
}
