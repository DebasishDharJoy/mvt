package com.mvt.backApp.common.validation;
import jakarta.validation.*;import java.lang.annotation.*;
@Documented @Constraint(validatedBy=PasswordMatchesValidator.class) @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches { String message() default "{validation.confirmPassword.mismatch}"; Class<?>[] groups() default {}; Class<? extends Payload>[] payload() default {}; }
