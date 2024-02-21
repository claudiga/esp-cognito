package com.esprofiler.claude.controller;

import com.esprofiler.claude.dto.AuthenticatedResponse;
import com.esprofiler.claude.service.AuthenticatedService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthenticatedController {

    private final AuthenticatedService authenticatedService;
    @GetMapping("/authenticated")
    public AuthenticatedResponse authenticated(JwtAuthenticationToken jwtAuthenticationToken) {
        return authenticatedService.createResponse(jwtAuthenticationToken,"Looks like you have been authenticated");
    }

}
