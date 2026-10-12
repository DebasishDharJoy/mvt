package com.mvt.backApp.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRegistrationDto {
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

    @NotBlank(message = "{validation.country.required}")
    @Size(max = 100, message = "{validation.country.size}")
    private String country;

    @Size(max = 100, message = "{validation.nationalId.size}")
    private String nationalId;

    @Size(max = 100, message = "{validation.passportNumber.size}")
    private String passportNumber;
}
