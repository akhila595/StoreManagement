package com.shopmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.dto.ForgotPasswordRequestDTO;
import com.shopmanagement.dto.LoginRequestDTO;
import com.shopmanagement.dto.RegisterRequestDTO;
import com.shopmanagement.service.AuthenticationService;
import com.shopmanagement.service.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public LoginController(AuthenticationService authenticationService, PasswordResetService passwordResetService) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
    }

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO request) {
        try {
            String response = authenticationService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Registration failed"));
        }
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Map<String, String> response = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }


    // ✅ Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            passwordResetService.sendPasswordResetLink(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password reset link sent to your email"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
    }
}
