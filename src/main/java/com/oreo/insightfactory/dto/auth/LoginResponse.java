package com.oreo.insightfactory.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oreo.insightfactory.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String token;
    private long expiresIn;
    private Role role;
    private String branch;
}

