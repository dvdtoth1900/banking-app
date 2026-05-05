package com.banking.controller;

import com.banking.model.SignupRequest;
import com.banking.service.AuthService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        try {
            authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully.");
        } catch (DuplicateKeyException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An account with this email already exists.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SignupRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        if (!authService.emailExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No account exists with this email.");
        }

        if (!authService.passwordMatches(request.getEmail(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
        }

        return ResponseEntity.ok("Login successful.");
    }
}