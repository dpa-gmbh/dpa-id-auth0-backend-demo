package com.dpa.dpaidauth0backenddemo.services.clients.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Auth0ValidateCodeResponseDTO {
    @Schema(name = "access_token", description = "Auth0 access_token")
    private String access_token;
    @Schema(name = "refresh_token", description = "Auth0 refresh_token")
    private String refresh_token;
    @Schema(name = "id_token", description = "Auth0 id_token")
    private String id_token;
    @Schema(name = "token_type", description = "Auth0 token_type field", example = "Bearer")
    private String token_type;
}
