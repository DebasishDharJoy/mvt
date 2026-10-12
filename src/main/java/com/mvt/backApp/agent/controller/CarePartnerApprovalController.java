package com.mvt.backApp.agent.controller;

import com.mvt.backApp.agent.dto.CarePartnerApplicationDetailDto;
import com.mvt.backApp.agent.dto.CarePartnerApplicationSummaryDto;
import com.mvt.backApp.agent.dto.CarePartnerDecisionResponseDto;
import com.mvt.backApp.agent.dto.CarePartnerRejectionRequestDto;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.service.CarePartnerApprovalService;
import com.mvt.backApp.common.dto.ApiResponse;
import com.mvt.backApp.common.security.PermissionNames;
import com.mvt.backApp.common.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/care-partner-applications")
@RequiredArgsConstructor
public class CarePartnerApprovalController {
    private final CarePartnerApprovalService service;
    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_VIEW)")
    public ResponseEntity<ApiResponse<Page<CarePartnerApplicationSummaryDto>>> findApplications(
            @RequestParam(defaultValue = "PENDING") ApprovalStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(success(
                messageService.getMessage("success.carePartner.applicationsFetched"),
                service.findApplications(status, pageable)
        ));
    }

    @GetMapping("/{partnerId}")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_VIEW)")
    public ResponseEntity<ApiResponse<CarePartnerApplicationDetailDto>> findOne(
            @PathVariable String partnerId
    ) {
        return ResponseEntity.ok(success(
                messageService.getMessage("success.carePartner.applicationFetched"),
                service.findByPartnerId(partnerId)
        ));
    }

    @PostMapping("/{partnerId}/approve")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_APPROVE)")
    public ResponseEntity<ApiResponse<CarePartnerDecisionResponseDto>> approve(
            @PathVariable String partnerId
    ) {
        return ResponseEntity.ok(success(
                messageService.getMessage("success.carePartner.approved"),
                service.approve(partnerId)
        ));
    }

    @PostMapping("/{partnerId}/reject")
    @PreAuthorize("hasAuthority(T(com.mvt.backApp.common.security.PermissionNames).CARE_PARTNER_APPLICATION_REJECT)")
    public ResponseEntity<ApiResponse<CarePartnerDecisionResponseDto>> reject(
            @PathVariable String partnerId,
            @Valid @RequestBody CarePartnerRejectionRequestDto request
    ) {
        return ResponseEntity.ok(success(
                messageService.getMessage("success.carePartner.rejected"),
                service.reject(partnerId, request.getReason())
        ));
    }

    private <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, HttpStatus.OK.value(), "success", data);
    }
}
