package com.example.server.service;

import com.example.server.config.JwtService;
import com.example.server.dto.authentication.*;
import com.example.server.exceptions.CustomResponse;
import com.example.server.model.Role;
import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final String DIFFERENT_PASSWORDS = "Passwords didn`t match";
    private final String USER_ALREADY_EXIST = "User with such email already exists";
    private final String INVALID_CREDENTIALS = "Invalid credentials";

    public ResponseEntity<?> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new ResponseEntity<>(new CustomResponse(DIFFERENT_PASSWORDS), HttpStatus.BAD_REQUEST);
        }
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            repository.save(user);
            String jwtToken = jwtService.generateToken(user);
            String jwtRefreshToken = jwtService.generateRefreshToken(user);
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(jwtRefreshToken)
                    .build());
        }
        catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse(USER_ALREADY_EXIST), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new CustomResponse(INVALID_CREDENTIALS), HttpStatus.UNAUTHORIZED);
        }

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(jwtRefreshToken)
                .build());
    }

    public ResponseEntity<?> refreshToken(RefreshRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
        User user = repository.findByEmail(username)
                .orElseThrow();
        return ResponseEntity.ok(RefreshResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .build());
    }
}
