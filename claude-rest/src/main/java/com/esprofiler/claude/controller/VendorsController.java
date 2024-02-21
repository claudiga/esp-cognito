package com.esprofiler.claude.controller;

import com.esprofiler.claude.dto.AuthenticatedResponse;
import com.esprofiler.claude.service.AuthenticatedService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class VendorsController {

    private final AuthenticatedService authenticatedService;
    @GetMapping("/vendors")
    public AuthenticatedResponse authenticated(JwtAuthenticationToken jwtAuthenticationToken) {
        return authenticatedService.createResponse(jwtAuthenticationToken, "Welcome to the vendor group!");
    }

    @GetMapping("/vendors/{vid}")
    public AuthenticatedResponse authenticatedVendors(@PathVariable(name = "vid") String vid, JwtAuthenticationToken jwtAuthenticationToken) {
        return authenticatedService.createResponseForAuthenticatedVendor(jwtAuthenticationToken, vid);
    }
}
