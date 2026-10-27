package com.mvt.backApp.user.controller;

import com.mvt.backApp.agent.dto.*;
import com.mvt.backApp.common.dto.*;
import com.mvt.backApp.common.service.ApiResponseFactory;
import com.mvt.backApp.patient.dto.*;
import com.mvt.backApp.user.dto.EmailOtpRequestDto;
import com.mvt.backApp.user.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;
    private final EmailOtpService otp;
    private final ApiResponseFactory responses;

    @PostMapping("/email-otp")
    public ResponseEntity<ApiResponse<Void>> otp(@Valid @RequestBody EmailOtpRequestDto r) {
        otp.send(r.getEmail(), r.getPurpose());
        return ResponseEntity.accepted().body(responses.success("success.otp.sent", 202, null));
    }

    @PostMapping(value = "/register/patient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientRegistrationResponseDto>> patient(@Valid @RequestBody PatientRegistrationDto r) {
        return ResponseEntity.status(201).body(responses.success("success.registration.patient", 201, auth.registerPatient(r)));
    }

    @PostMapping(value = "/register/care-partner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CarePartnerRegistrationResponseDto>> partner(@Valid @ModelAttribute CarePartnerRegistrationDto r) {
        return ResponseEntity.status(201).body(responses.success("success.registration.carePartner", 201, auth.registerCarePartner(r)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto r) {
        return ResponseEntity.ok(responses.success("success.auth.login", 200, auth.login(r)));
    }
}
