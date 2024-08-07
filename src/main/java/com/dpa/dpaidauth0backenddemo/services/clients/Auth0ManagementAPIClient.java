package com.dpa.dpaidauth0backenddemo.services.clients;


import com.dpa.dpaidauth0backenddemo.services.clients.dto.Auth0ValidateCodeResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

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
      MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
      map.put("code", List.of(code));
      map.put("client_id", List.of(clientId));
      map.put("client_secret", List.of(clientSecret));
      map.put("grant_type", List.of("authorization_code"));
      map.put("redirect_uri", List.of("https://backend-demo.dpa-id.de"));
      return createClient()
              .post()
              .uri("/oauth/token")
              .body(map)
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .retrieve()
              .body(Auth0ValidateCodeResponseDTO.class);
    }



}
