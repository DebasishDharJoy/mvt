package com.mvt.backApp.agent.dto;

import com.mvt.backApp.agent.entity.IdentityDocumentType;
import com.mvt.backApp.common.validation.*;
import com.mvt.backApp.user.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@PasswordMatches
public class CarePartnerRegistrationDto implements PasswordConfirmation {
    @NotBlank(message = "{validation.firstName.required}")
    @Size(max = 100, message = "{validation.firstName.size}")
    private String firstName;
    @NotBlank(message = "{validation.lastName.required}")
    @Size(max = 100, message = "{validation.lastName.size}")
    private String lastName;
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    @NotNull(message = "{validation.gender.required}")
    private Gender gender;
    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 72, message = "{validation.password.size}")
    @StrongPassword
    private String password;
    @NotBlank(message = "{validation.confirmPassword.required}")
    private String confirmPassword;
    @NotBlank(message = "{validation.companyName.required}")
    @Size(max = 200, message = "{validation.companyName.size}")
    private String companyName;
    @NotBlank(message = "{validation.countryCode.required}")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "{validation.countryCode.invalid}")
    private String countryCode;
    @NotBlank(message = "{validation.mobileNumber.required}")
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "{validation.mobileNumber.invalid}")
    private String mobileNumber;
    private boolean whatsappSameAsMobile;
    @Pattern(regexp = "^$|^\\+[1-9]\\d{7,14}$", message = "{validation.whatsappNumber.invalid}")
    private String whatsappNumber;
    @NotNull(message = "{validation.documentType.required}")
    private IdentityDocumentType documentType;
    @NotBlank(message = "{validation.documentNumber.required}")
    @Size(max = 150, message = "{validation.documentNumber.size}")
    private String documentNumber;
    @NotNull(message = "{validation.verificationDocument.required}")
    private MultipartFile verificationDocument;
    @NotBlank(message = "{validation.emailOtp.required}")
    @Pattern(regexp = "^\\d{6}$", message = "{validation.emailOtp.invalid}")
    private String emailOtp;
}
