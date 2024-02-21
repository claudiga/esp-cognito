package com.esprofiler.claude.controller;
import com.esprofiler.claude.service.AuthenticatedService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticatedControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticatedService authenticatedService; // Mock the service layer used by the controller

    @MockBean
    private JwtDecoder jwtDecoder;

    private JwtAuthenticationToken createMockJwtAuthenticationToken() {
        Jwt jwt = createJwt();
        return new JwtAuthenticationToken(jwt, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Jwt createJwt(){
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .subject("user")
                .claim("authorities", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .claim("cognito:groups","VENDOR")
                .expiresAt(Instant.now().plusSeconds(3600)) // Set expiration to future
                .build();

        return jwt;
    }

    // Method to return a RequestPostProcessor that adds the JwtAuthenticationToken to the SecurityContext
    private RequestPostProcessor jwtAuth() {
        return mockRequest -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createMockJwtAuthenticationToken());
            SecurityContextHolder.setContext(context);
            return mockRequest;
        };
    }

    // Example test method using the above utility
    @Test
    public void whenAuthenticatedAccess_thenSucceedsWith200() throws Exception {
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt());


        mockMvc.perform(get("/api/v1/authenticated")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                 .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isOk());
    }


    @Test
    public void whenUnauthenticatedAccess_thenFailsWith401() throws Exception {
        mockMvc.perform(get("/api/v1/authenticated")) // Update the path as per your actual controller mapping
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void whenHasVendorGroupAccess_thenSucceedsWith200() throws Exception {
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt());


        mockMvc.perform(get("/api/v1/vendors")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                        .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isOk());
    }
}