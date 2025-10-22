package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.auth.LoginRequest;
import com.oreo.insightfactory.dto.auth.LoginResponse;
import com.oreo.insightfactory.dto.auth.RegisterRequest;
import com.oreo.insightfactory.dto.auth.RegisterResponse;
import com.oreo.insightfactory.exception.ConflictException;
import com.oreo.insightfactory.exception.ValidationException;
import com.oreo.insightfactory.model.Role;
import com.oreo.insightfactory.model.User;
import com.oreo.insightfactory.repository.UserRepository;
import com.oreo.insightfactory.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Validate role-specific requirements
        if (request.getRole() == Role.BRANCH && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new ValidationException("Branch is required for BRANCH role");
        }
        if (request.getRole() == Role.CENTRAL && request.getBranch() != null) {
            throw new ValidationException("Branch must be null for CENTRAL role");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .branch(request.getBranch())
                .build();

        user = userRepository.save(user);

        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .branch(user.getBranch())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole())
                .branch(user.getBranch())
                .build();
    }
}

