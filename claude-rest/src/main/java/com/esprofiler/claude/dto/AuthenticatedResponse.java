package com.esprofiler.claude.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthenticatedResponse {

    private String message;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String vendorId;
    private List<String> grantedAuthorities;
}
