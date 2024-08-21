package com.dpa.dpaidauth0backenddemo.services.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Auth0AccessTokenDTO {
    private String access_token;
    private String expires_in;
    private String token_type;
}