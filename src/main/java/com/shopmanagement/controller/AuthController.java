package com.shopmanagement.controller;

import com.shopmanagement.dto.ForgotPasswordRequestDTO;
import com.shopmanagement.dto.LoginRequestDTO;
import com.shopmanagement.dto.RegisterRequestDTO;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.UserRepository;
import com.shopmanagement.service.AuthenticationService;
import com.shopmanagement.service.JwtUtil;
import com.shopmanagement.service.PasswordResetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationService authenticationService,
                          PasswordResetService passwordResetService,
                          UserRepository userRepository,
                          JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ------------------------------------
    //  REGISTER (Default role = USER)
    // ------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        String msg = authenticationService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // ------------------------------------
    //  LOGIN (returns token + roles)
    // ------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        return ResponseEntity.ok(authenticationService.authenticate(
                req.getEmail(),
                req.getPassword()
        ));
    }

    // ------------------------------------
    //  CURRENT USER (decoded from JWT)
    // ------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                Map.of(
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "roles", user.getRoleNames()
                )
        );
    }

    // ------------------------------------
    //  FORGOT PASSWORD
    // ------------------------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset link sent"));
    }
}
