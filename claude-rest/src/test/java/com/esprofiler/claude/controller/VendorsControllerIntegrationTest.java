package com.esprofiler.claude.controller;

import com.esprofiler.claude.service.AuthenticatedService;
import com.esprofiler.claude.service.cognito.CognitoClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VendorsControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CognitoClient cognitoClient;

    private JwtAuthenticationToken createMockJwtAuthenticationToken() {
        Jwt jwt = createJwt(e->{});
        return new JwtAuthenticationToken(jwt, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Jwt createJwt(Consumer<Jwt.Builder> customizer){
        Jwt.Builder builder = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .subject("user")
                .expiresAt(Instant.now().plusSeconds(3600));// Set expiration to future
                customizer.accept(builder);

        return builder.build();
    }

    private RequestPostProcessor jwtAuth() {
        return mockRequest -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createMockJwtAuthenticationToken());
            SecurityContextHolder.setContext(context);
            return mockRequest;
        };
    }

    @Test
    public void whenHasVendorGroupAccess_thenSucceedsWith200() throws Exception {
        Map<String, Object> mockUserInfo = new HashMap<>();
        mockUserInfo.put("given_name", "John");
        mockUserInfo.put("family_name", "Doe");
        Mockito.when(cognitoClient.getUserInfo(Mockito.anyString())).thenReturn(mockUserInfo);
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt(
                builder ->  builder.claim("cognito:groups", "VENDOR")
        ));


        mockMvc.perform(get("/api/v1/vendors")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                        .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isOk());
    }

    @Test
    public void whenHasNoVendorGroupAccess_thenFailsWith401() throws Exception {
        Map<String, Object> mockUserInfo = new HashMap<>();
        mockUserInfo.put("given_name", "John");
        mockUserInfo.put("family_name", "Doe");
        Mockito.when(cognitoClient.getUserInfo(Mockito.anyString())).thenReturn(mockUserInfo);
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt(
                (e)-> {}
        ));


        mockMvc.perform(get("/api/v1/vendors")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                        .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isForbidden());
    }


    @Test
    public void whenHasSpecificVendorAccess_thenSucceedsWith200() throws Exception {
        Map<String, Object> mockUserInfo = new HashMap<>();
        mockUserInfo.put("given_name", "John");
        mockUserInfo.put("family_name", "Doe");
        mockUserInfo.put("custom:VID", "test-vid");
        Mockito.when(cognitoClient.getUserInfo(Mockito.anyString())).thenReturn(mockUserInfo);
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt(

                builder ->  builder.claim("cognito:groups", "VENDOR")
        ));


        mockMvc.perform(get("/api/v1/vendors/test-vid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                        .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isOk());
    }

    @Test
    public void whenHasNoSpecificVendorAccess_thenFailsWith401() throws Exception {
        Map<String, Object> mockUserInfo = new HashMap<>();
        mockUserInfo.put("given_name", "John");
        mockUserInfo.put("family_name", "Doe");
        mockUserInfo.put("custom:VID", "test-vid");
        Mockito.when(cognitoClient.getUserInfo(Mockito.anyString())).thenReturn(mockUserInfo);
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(createJwt(e->{}));


        mockMvc.perform(get("/api/v1/vendors/test-vid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")

                        .with(jwtAuth())
                ) // Apply the JwtAuthenticationToken to this request
                .andExpect(status().isForbidden());
    }
}