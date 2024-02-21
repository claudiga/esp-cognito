package com.esprofiler.claude.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnauthenticatedResponse {

    private String message;
    private String name;
}
