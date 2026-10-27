package com.mvt.backApp.user.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class ChangeManagedPasswordRequest {
 @NotBlank(message="{validation.temporaryPassword.required}")
 @Size(min=12,max=72,message="{validation.temporaryPassword.size}") private String temporaryPassword;
}
