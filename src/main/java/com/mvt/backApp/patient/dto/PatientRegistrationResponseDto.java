package com.mvt.backApp.patient.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PatientRegistrationResponseDto {
    String patientId;
    String email;
}
