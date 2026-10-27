package com.mvt.backApp.validation;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.agent.dto.CarePartnerRejectionRequestDto;
import com.mvt.backApp.common.dto.LoginRequestDto;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.user.dto.AssignPermissionsRequest;
import com.mvt.backApp.user.dto.ChangeManagedPasswordRequest;
import com.mvt.backApp.user.dto.CreateAdminRequest;
import com.mvt.backApp.user.dto.EmailOtpRequestDto;
import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMessageKeyContractTest {

    private static final List<Class<?>> DTOS = List.of(
            PatientRegistrationDto.class,
            CarePartnerRegistrationDto.class,
            CarePartnerRejectionRequestDto.class,
            LoginRequestDto.class,
            EmailOtpRequestDto.class,
            CreateAdminRequest.class,
            AssignPermissionsRequest.class,
            ChangeManagedPasswordRequest.class
    );

    @Test
    void everyConstraintUsesExplicitMessageKey() {
        List<String> invalidMessages = new ArrayList<>();

        for (Class<?> dtoType : DTOS) {
            inspectAnnotations(
                    dtoType,
                    dtoType.getAnnotations(),
                    invalidMessages
            );

            for (Field field : dtoType.getDeclaredFields()) {
                inspectAnnotations(
                        dtoType,
                        field.getAnnotations(),
                        invalidMessages,
                        field.getName()
                );
            }
        }

        assertThat(invalidMessages)
                .as("Every DTO constraint must use a {validation.*} key")
                .isEmpty();
    }

    private void inspectAnnotations(
            Class<?> dtoType,
            Annotation[] annotations,
            List<String> invalidMessages,
            String... fieldName
    ) {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType()
                    .isAnnotationPresent(Constraint.class)) {
                continue;
            }

            String location = dtoType.getSimpleName();
            if (fieldName.length == 1) {
                location += "." + fieldName[0];
            }

            String message = readMessage(annotation);
            if (message == null
                    || !message.matches(
                    "\\{validation\\.[A-Za-z0-9.]+}"
            )) {
                invalidMessages.add(location + " -> " + message);
            }
        }
    }

    private String readMessage(Annotation annotation) {
        try {
            Method method = annotation.annotationType().getMethod("message");
            return (String) method.invoke(annotation);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(
                    "Could not read validation message from "
                            + annotation.annotationType().getName(),
                    exception
            );
        }
    }
}
