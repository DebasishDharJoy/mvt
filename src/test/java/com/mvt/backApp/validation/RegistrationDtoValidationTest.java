package com.mvt.backApp.validation;

import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationDtoValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void missingPatientFieldsExposeConfiguredKeys() {
        PatientRegistrationDto d = new PatientRegistrationDto();
        var messages = validator.validate(d).stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toSet());
        assertThat(messages).contains("{validation.firstName.required}", "{validation.lastName.required}", "{validation.email.required}", "{validation.gender.required}", "{validation.password.required}", "{validation.confirmPassword.required}", "{validation.countryCode.required}", "{validation.mobileNumber.required}", "{validation.emailOtp.required}");
    }
}
