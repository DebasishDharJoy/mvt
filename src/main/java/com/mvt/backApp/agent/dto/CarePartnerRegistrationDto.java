package com.mvt.backApp.agent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CarePartnerRegistrationDto {

    @NotBlank(message = "{validation.title.required}")
    @Size(max = 10, message = "{validation.title.size}")
    private String title;

    @NotBlank(message = "{validation.firstName.required}")
    @Size(max = 100, message = "{validation.firstName.size}")
    private String firstName;

    @Size(max = 100, message = "{validation.middleName.size}")
    private String middleName;

    @NotBlank(message = "{validation.lastName.required}")
    @Size(max = 100, message = "{validation.lastName.size}")
    private String lastName;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 72, message = "{validation.password.size}")
    private String password;

    private String companyName;

    @NotBlank(message = "{validation.country.required}")
    private String country;

    private String tradeLicense;
    private String gstNumber;
    private String passportNumber;

    @NotNull(message = "{validation.verificationDocument.required}")
    private MultipartFile verificationDocument;
}

