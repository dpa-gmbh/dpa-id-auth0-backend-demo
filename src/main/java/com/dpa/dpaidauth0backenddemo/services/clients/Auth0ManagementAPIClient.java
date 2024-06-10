package com.dpa.dpaidauth0backenddemo.services.clients;


import com.dpa.dpaidauth0backenddemo.services.clients.dto.Auth0ValidateCodeResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class Auth0ManagementAPIClient {
    @Value("${spring.security.managementAPI.client.provider.dpaid.id:N/A}")
    private String clientId;

    @Value("${spring.security.managementAPI.client.provider.dpaid.secret:N/A}")
    private String clientSecret;

    @Value("${spring.security.managementAPI.url:N/A}")
    private String managementAPIUrl;

    @Value("${spring.security.managementAPI.client.provider.dpaid.issuer-uri:N/A}")
    private String issuerUri;
    private static final ObjectMapper mapper = new ObjectMapper();


    public RestClient createClient() {
        return RestClient.builder().baseUrl(managementAPIUrl).build();
    }

    public Auth0ValidateCodeResponseDTO validateCode(String code) {
        try {
            return createClient()
                    .post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(mapper.writeValueAsString(Map.of(
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "grant_type", "authorization_code",
                            "code", code)))
                    .retrieve()
                    .body(Auth0ValidateCodeResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
