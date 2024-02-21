package com.esprofiler.claude.service.cognito;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CognitoClient {

    public Map<String, Object> getUserInfo(String accessToken) {
        String cognitoUserInfoEndpoint = "https://esp-production-test.auth.eu-west-2.amazoncognito.com/oauth2/userInfo";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(cognitoUserInfoEndpoint, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve user info");
        }
    }
}