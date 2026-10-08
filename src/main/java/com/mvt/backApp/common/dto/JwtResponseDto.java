package com.mvt.backApp.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String role;
}
