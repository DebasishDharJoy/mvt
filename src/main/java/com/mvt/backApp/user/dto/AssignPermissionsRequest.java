package com.mvt.backApp.user.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;
@Data public class AssignPermissionsRequest {
 @NotNull(message="{validation.permissions.required}")
 private Set<@NotBlank(message="{validation.permissionName.required}") String> permissions;
}
