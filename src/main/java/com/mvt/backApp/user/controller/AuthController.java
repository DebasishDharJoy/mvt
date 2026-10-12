package com.mvt.backApp.user.controller;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.agent.dto.CarePartnerRegistrationResponseDto;
import com.mvt.backApp.agent.dto.CarePartnerRejectionRequestDto;
import com.mvt.backApp.common.dto.ApiResponse;
import com.mvt.backApp.common.dto.JwtResponseDto;
import com.mvt.backApp.common.dto.LoginRequestDto;
import com.mvt.backApp.common.service.MessageService;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.patient.dto.PatientRegistrationResponseDto;
import com.mvt.backApp.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MessageService messageService;

    @PostMapping(value = "/register/patient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientRegistrationResponseDto>> registerPatient(
            @Valid @RequestBody PatientRegistrationDto request
    ) {
        PatientRegistrationResponseDto data = authService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                messageService.getMessage("success.registration.patient"),
                HttpStatus.CREATED.value(),
                "success",
                data
        ));
    }

    @PostMapping(value = "/register/care-partner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CarePartnerRegistrationResponseDto>> registerCarePartner(
            @Valid @ModelAttribute CarePartnerRegistrationDto request
    ) {
        CarePartnerRegistrationResponseDto data = authService.registerCarePartner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                messageService.getMessage("success.registration.carePartner"),
                HttpStatus.CREATED.value(),
                "success",
                data
        ));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        JwtResponseDto data = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getMessage("success.auth.login"),
                HttpStatus.OK.value(),
                "success",
                data
        ));
    }
}
