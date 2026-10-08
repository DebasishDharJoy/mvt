package com.mvt.backApp.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CarePartnerRegistrationDto {
    @NotBlank private String title;
    @NotBlank private String firstName;
    private String middleName;
    @NotBlank private String lastName;

    @NotBlank private String email;
    @NotBlank private String password;

    private String companyName;
    @NotBlank private String country;

    private String tradeLicense;
    private String gstNumber;
    private String passportNumber;

    // The file uploaded from the frontend
    @NotNull(message = "Verification document is required")
    private MultipartFile verificationDocument;
}