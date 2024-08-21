package com.dpa.dpaidauth0backenddemo.services.clients;


import com.dpa.dpaidauth0backenddemo.services.clients.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
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

  private final String redirectUri = "https://backend-demo.dpa-id.de";

  private static final ObjectMapper mapper = new ObjectMapper();


  public RestClient createClient() {
    return RestClient.builder().baseUrl(managementAPIUrl).build();
  }

  private Auth0AccessTokenDTO getAccessToken() {
    try {
      return createClient()
          .post()
          .uri("/oauth/token")
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(Map.of("client_id", clientId,
              "client_secret", clientSecret,
              "grant_type", "client_credentials",
              "audience", issuerUri)))
          .retrieve()
          .body(Auth0AccessTokenDTO.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Auth0ValidateCodeResponseDTO validateCode(String code) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put("code", List.of(code));
    map.put("client_id", List.of(clientId));
    map.put("client_secret", List.of(clientSecret));
    map.put("grant_type", List.of("authorization_code"));
    map.put("redirect_uri", List.of(redirectUri));
    return createClient()
        .post()
        .uri("/oauth/token")
        .body(map)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .retrieve()
        .body(Auth0ValidateCodeResponseDTO.class);
  }


  public SessionStatusResponseDTO checkUserSessionStatus(String userAccessToken) throws JsonProcessingException {
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String[] chunks = userAccessToken.split("\\.");
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    AccessTokenPayloadDTO accessTokenPayloadDTO = mapper.readValue(new String(decoder.decode(chunks[1])), AccessTokenPayloadDTO.class);
    String format = String.format("/api/v2/users/%s/sessions", accessTokenPayloadDTO.getSub());
    Auth0AccessTokenDTO accessToken = getAccessToken();
    SessionResponseDTO sessionResponseDTO = createClient()
        .get()
        .uri(format)
        .header("Authorization", Strings.concat("Bearer ", accessToken.getAccess_token()))
        .retrieve()
        .body(SessionResponseDTO.class);
    List<SessionDTO> sessions = sessionResponseDTO.getSessions();

    if (sessions == null || sessions.isEmpty()) {
      return new SessionStatusResponseDTO(false);
    }
    boolean isSessionExpired = sessions.stream()
        .filter(it -> it.getClients()
            .stream()
            .anyMatch(itc -> accessTokenPayloadDTO.getAzp().equals(itc.getClient_id())))
        .anyMatch(it -> LocalDateTime.parse(it.getExpires_at(), DateTimeFormatter.ISO_DATE_TIME).isBefore(LocalDateTime.now()));

    if (isSessionExpired) {
      return new SessionStatusResponseDTO(false);
    }


    return new SessionStatusResponseDTO(true);
  }
}
