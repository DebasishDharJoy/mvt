package com.mvt.backApp.user.dto;
import com.mvt.backApp.user.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;
@Data public class CreateAdminRequest {
 @NotBlank(message="{validation.firstName.required}") @Size(max=100,message="{validation.firstName.size}") private String firstName;
 @NotBlank(message="{validation.lastName.required}") @Size(max=100,message="{validation.lastName.size}") private String lastName;
 @NotNull(message="{validation.gender.required}") private Gender gender;
 @NotBlank(message="{validation.email.required}") @Email(message="{validation.email.invalid}") private String email;
 @NotBlank(message="{validation.temporaryPassword.required}") @Size(min=12,max=72,message="{validation.temporaryPassword.size}") private String temporaryPassword;
 @NotNull(message="{validation.permissions.required}") private Set<@NotBlank(message="{validation.permissionName.required}") String> permissions;
}
