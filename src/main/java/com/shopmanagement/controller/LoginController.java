package com.shopmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.dto.ForgotPasswordRequestDTO;
import com.shopmanagement.dto.LoginRequestDTO;
import com.shopmanagement.dto.RegisterRequestDTO;
import com.shopmanagement.service.AuthenticationService;
import com.shopmanagement.service.PasswordResetService;

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
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        try {
            String response = authenticationService.register(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String token = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // ✅ Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            passwordResetService.sendPasswordResetLink(request.getEmail());
            return ResponseEntity.ok("Password reset link sent to your email");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
