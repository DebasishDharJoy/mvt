package com.mvt.backApp.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class JwtResponseDto {
    private String token;

    @Builder.Default
    private String type = "Bearer";

    private UUID userId;
    private String email;
    private String role;
    private List<String> permissions;
}
