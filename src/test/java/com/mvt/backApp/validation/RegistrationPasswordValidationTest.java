package com.mvt.backApp.validation;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationPasswordValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requiredKeys() {
        PatientRegistrationDto d = new PatientRegistrationDto();
        assertThat(keys(validator.validate(d))).contains("{validation.password.required}", "{validation.confirmPassword.required}");
    }

    @Test
    void lengthKey() {
        PatientRegistrationDto d = base();
        d.setPassword("Aa1!");
        d.setConfirmPassword("Aa1!");
        assertThat(keys(validator.validate(d))).contains("{validation.password.size}");
    }

    @Test
    void complexityKey() {
        PatientRegistrationDto d = base();
        d.setPassword("password1!");
        d.setConfirmPassword("password1!");
        assertThat(keys(validator.validate(d))).contains("{validation.password.complexity}");
    }

    @Test
    void mismatchTargetsConfirmPassword() {
        PatientRegistrationDto d = base();
        d.setConfirmPassword("Different1!");
        var v = validator.validate(d).stream().filter(x -> x.getMessageTemplate().equals("{validation.confirmPassword.mismatch}")).findFirst().orElseThrow();
        assertThat(v.getPropertyPath().toString()).isEqualTo("confirmPassword");
    }

    @Test
    void partnerUsesMismatchContract() {
        CarePartnerRegistrationDto d = new CarePartnerRegistrationDto();
        d.setPassword("StrongPass1!");
        d.setConfirmPassword("Different1!");
        assertThat(keys(validator.validate(d))).contains("{validation.confirmPassword.mismatch}");
    }

    @Test
    void strongMatchingPasswordPassesPasswordRules() {
        assertThat(keys(validator.validate(base()))).doesNotContain("{validation.password.required}", "{validation.password.size}", "{validation.password.complexity}", "{validation.confirmPassword.required}", "{validation.confirmPassword.mismatch}");
    }

    private PatientRegistrationDto base() {
        PatientRegistrationDto d = new PatientRegistrationDto();
        d.setPassword("StrongPass1!");
        d.setConfirmPassword("StrongPass1!");
        return d;
    }

    private Set<String> keys(Set<? extends ConstraintViolation<?>> v) {
        return v.stream().map(ConstraintViolation::getMessageTemplate).collect(java.util.stream.Collectors.toSet());
    }
}
