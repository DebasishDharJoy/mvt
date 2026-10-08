package com.mvt.backApp.user.controller;

import com.mvt.backApp.common.dto.ApiResponse;
import com.mvt.backApp.common.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Notice we use @ModelAttribute instead of @RequestBody to handle files
    @PostMapping(value = "/register/care-partner", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> registerCarePartner(
            @Valid @ModelAttribute CarePartnerRegistrationDto request) {

        String partnerId = authService.registerCarePartner(request);

        ApiResponse<String> response = new ApiResponse<>(
                "Registration successful. Verification document uploaded. Awaiting Master Admin approval.",
                HttpStatus.CREATED.value(),
                "success",
                "Partner ID: " + partnerId
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
