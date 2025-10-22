package com.oreo.insightfactory.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oreo.insightfactory.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private String branch;
    private LocalDateTime createdAt;
}

