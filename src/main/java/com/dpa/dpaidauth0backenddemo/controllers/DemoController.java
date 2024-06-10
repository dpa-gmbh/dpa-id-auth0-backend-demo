package com.dpa.dpaidauth0backenddemo.controllers;

import com.dpa.dpaidauth0backenddemo.services.clients.Auth0ManagementAPIClient;
import com.dpa.dpaidauth0backenddemo.services.clients.dto.Auth0ValidateCodeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                                         @RequestParam("code") String code) {
        Auth0ValidateCodeResponseDTO auth0CodeResponseDTO = auth0ManagementAPIClient.validateCode(code);
        return ResponseEntity.ok(auth0CodeResponseDTO);
    }
}
