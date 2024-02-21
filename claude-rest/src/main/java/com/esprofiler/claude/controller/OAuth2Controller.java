package com.esprofiler.claude.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
public class OAuth2Controller {

    @Value("${spring.security.oauth2.client.provider.cognito.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String redirectUri;


    @GetMapping("/")
    public ResponseEntity<OAuth2AccessToken> exchangeCodeForToken(@RequestParam String code,
                                                                  @RequestParam(required = false) String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", List.of("authorization_code"));
        params.put("client_id", List.of(clientId));
        params.put("code", List.of(code));
        // The redirect URI must match the one registered in Cognito and used in the authorization request
        params.put("redirect_uri", List.of(redirectUri));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ParameterizedTypeReference<Map<String, Object>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Map<String,Object>> response = restTemplate().exchange(tokenUri,HttpMethod.POST, request, parameterizedTypeReference);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            OAuth2AccessToken token = new OAuth2AccessToken(TokenType.BEARER,
                    responseBody.get("access_token").toString(),
                    Instant.now(),
                    Instant.now().plusSeconds(Long.parseLong(responseBody.get("expires_in").toString())));
            return ResponseEntity.ok(token);
        } else {
            // Handle error scenario
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // RestTemplate Bean

    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
