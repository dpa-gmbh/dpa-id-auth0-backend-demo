package com.dpa.dpaidauth0backenddemo.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjFaR29TMndNTUloTzNCaUJiNzYtViJ9.eyJpc3MiOiJodHRwczovL2RwYS1pZC1kZXZlbC5ldS5hdXRoMC5jb20vIiwic3ViIjoibUtVMWNaN2wzUG5KdFhyVk9MZlY5Q0VOY3A0NG5wR1JAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vZHBhLWlkLWRldmVsLmV1LmF1dGgwLmNvbS9hcGkvdjIvIiwiaWF0IjoxNzE3Njc4NzQ1LCJleHAiOjE3MTc3NjUxNDUsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6Im1LVTFjWjdsM1BuSnRYclZPTGZWOUNFTmNwNDRucEdSIn0.WH9qr-Qv5ciy249GWaT4GwTKsX4ZnsO3N1ug2wJAuQdaw_SO_COYA1YjuXEwJmD25_seakMFE8qXHim2K3JngFkYjoTC_0fpJkXo6mKLCw26zKuPxSme7l-W0ZAQnPooLAcrHw3i6twYxfw7Qz0kkO1emOlrUhpFjskim7Mllmf4nGETxJorZq7pPKw5YDFkG3SJ8ph6FjOSw3ES7KXSI6l6_6JUXtaH-bTtZkROZ-PEN9ixz_EhH-fVfBM-QJud3eBSE3bbh3jlAWSNwo2clHGgtvay-fYzUgQ2Fae1GB7-PRb9t41e2igK93sMYsNbSqY5f_k032kULIUyJRIkWA";

    @Test
    void shouldReturn4XXErrorWhenForLoginWithoutAuthorizationToken() throws Exception {
        mvc.perform(get("/login/v1/spa").contentType("application/json"))
                .andExpect(status().is4xxClientError()).andReturn();
    }
}