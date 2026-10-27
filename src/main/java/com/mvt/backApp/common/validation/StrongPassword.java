package com.mvt.backApp.common.validation;
import jakarta.validation.*;import java.lang.annotation.*;
@Documented @Constraint(validatedBy=StrongPasswordValidator.class) @Target({ElementType.FIELD,ElementType.PARAMETER}) @Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword { String message() default "{validation.password.complexity}"; Class<?>[] groups() default {}; Class<? extends Payload>[] payload() default {}; }
