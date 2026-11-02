package com.mvt.backApp.user.dto;
import com.mvt.backApp.user.entity.OtpPurpose;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class EmailOtpRequestDto {
 @NotBlank(message="{validation.email.required}")
 @Email(message="{validation.email.invalid}") private String email;
 @NotNull(message="{validation.otpPurpose.required}") private OtpPurpose purpose;
}
