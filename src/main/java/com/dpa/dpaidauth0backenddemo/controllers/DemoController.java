package com.dpa.dpaidauth0backenddemo.controllers;

import com.dpa.dpaidauth0backenddemo.services.clients.Auth0ManagementAPIClient;
import com.dpa.dpaidauth0backenddemo.services.clients.dto.Auth0ValidateCodeResponseDTO;
import com.dpa.dpaidauth0backenddemo.services.clients.dto.SessionStatusResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(path = "/authorization/v1", produces = "application/json")
@CrossOrigin(origins = "*")
public class DemoController {

    private final Auth0ManagementAPIClient auth0ManagementAPIClient;

    @Autowired
    public DemoController(Auth0ManagementAPIClient auth0ManagementAPIClient) {
        this.auth0ManagementAPIClient = auth0ManagementAPIClient;
    }

    @Operation(summary = "Example endpoint for SPA. Auth0 access_token should be provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a message", content =
                    { @Content(mediaType = "application/json", schema = @Schema(example = "Hello World")) }),
            @ApiResponse(responseCode = "401", description = "Access token is invalid", content =
                    { @Content(mediaType = "application/json", schema = @Schema(example = "ERROR: Access token is invalid")) })
    })
    @GetMapping(value = "/spa/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World");
    }

    @Operation(summary = "Validate Auth0 code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns auth0 tokens"),
            @ApiResponse(responseCode = "500", description = "Code is not valid", content =
                    { @Content(mediaType = "application/json", schema = @Schema(example = "ERROR: Code is not valid")) })
    })
    @GetMapping("/rwa/code/validate")
    public ResponseEntity<Auth0ValidateCodeResponseDTO> validateCode(@Schema(name = "code", description = "The code that auth0 returns")
                                                                         @RequestParam("code") String code, @RequestParam("state") String state) {
        Auth0ValidateCodeResponseDTO auth0CodeResponseDTO = auth0ManagementAPIClient.validateCode(code);
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        String redirectUri = getRedirectUri(state);
        String uri =  redirectUri + "&access_token=" + auth0CodeResponseDTO.getAccess_token() + "&id_token=" + auth0CodeResponseDTO.getId_token();
        headers.put("Location", List.of(uri));
      return new ResponseEntity<>(headers, HttpStatus.valueOf(302));
    }

    @Operation(summary = "Check session is valid for a user. Provide access_token for authorization.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns session is valid or invalid"),
    })
    @GetMapping("/rwa/sessions/status")
    public ResponseEntity<SessionStatusResponseDTO> checkSessions(@RequestHeader("Authorization") String access_token) throws JsonProcessingException {
        return ResponseEntity.ok(auth0ManagementAPIClient.checkUserSessionStatus(access_token));
    }

    private String getRedirectUri(String state) {
        if(StringUtils.hasLength(state)){
            String decode = URLDecoder.decode(state, StandardCharsets.UTF_8);
            if(decode.contains("http")){
                return decode;
            }
        }
        return "https://rwa-demo.dpa-id.de";
    }
}
