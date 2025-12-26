package com.shopmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.service.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/superadmin/context")
@CrossOrigin
public class SuperAdminContextController {

    private final JwtUtils jwtUtils;

    public SuperAdminContextController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/select-customer/{customerId}")
    public ResponseEntity<?> selectCustomer(
            @PathVariable Long customerId,
            HttpServletRequest request) {

        if (!jwtUtils.isCurrentUserSuperAdmin()) {
            return ResponseEntity.status(403).body("Only Superadmin allowed");
        }

        // Just validation – real customerId comes via header in future calls
        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Customer selected successfully",
                        "customerId", customerId
                )
        );
    }
}
