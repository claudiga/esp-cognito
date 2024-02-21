package com.esprofiler.claude.service;


import com.esprofiler.claude.dto.AuthenticatedResponse;
import com.esprofiler.claude.exception.UnauthorizedException;
import com.esprofiler.claude.service.cognito.CognitoClient;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthenticatedService {

    private final CognitoClient cognitoClient;
    public AuthenticatedResponse createResponse(JwtAuthenticationToken jwtAuthenticationToken, String message){
        Map<String, Object> userInfo = cognitoClient.getUserInfo(jwtAuthenticationToken.getToken().getTokenValue());
        String name = userInfo.get("given_name")+"_"+userInfo.get("family_name");
        List<String> authorities = jwtAuthenticationToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return AuthenticatedResponse
                .builder()
                .name(name)
                .message(message)
                .grantedAuthorities(authorities)
                .build();
    }

    public AuthenticatedResponse createResponseForAuthenticatedVendor(JwtAuthenticationToken jwtAuthenticationToken, String vidPath) {
        Map<String, Object> userInfo = cognitoClient.getUserInfo(jwtAuthenticationToken.getToken().getTokenValue());
        String userVid = (String) userInfo.get("custom:VID");
        if(!userVid.equals(vidPath)){
            throw new UnauthorizedException("Access denied");
        }
        String name = userInfo.get("given_name")+"_"+userInfo.get("family_name");
        List<String> authorities = jwtAuthenticationToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return AuthenticatedResponse
                .builder()
                .name(name)
                .message("Looks like you’re a specific vendor!")
                .grantedAuthorities(authorities)
                .vendorId(userVid)
                .build();
    }
}
