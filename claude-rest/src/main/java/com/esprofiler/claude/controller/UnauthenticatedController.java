package com.esprofiler.claude.controller;

import com.esprofiler.claude.dto.UnauthenticatedResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UnauthenticatedController {

    @GetMapping("/unauthenticated")
    public UnauthenticatedResponse unauthenticated() {
        return new UnauthenticatedResponse("Free for all to see", "Anonymous");
    }

}
