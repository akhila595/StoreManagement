package com.shopmanagement.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    // ==========================================================
    // 🔐 Get Current Authenticated User ID
    // ==========================================================
    public Long getUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }

        return null;
    }

    // ==========================================================
    // 🔐 Get Current Customer ID
    // ==========================================================
    public Long getCustomerId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal userPrincipal) {
            return userPrincipal.getCustomerId();
        }

        return null;
    }

    // ==========================================================
    // 🔐 Required Customer ID
    // ==========================================================
    public Long getRequiredCustomerId() {

        Long customerId = getCustomerId();

        if (customerId == null) {
            throw new IllegalStateException("Unauthorized or customer not selected");
        }

        return customerId;
    }

    // ==========================================================
    // 🔐 SuperAdmin Check
    // ==========================================================
    public boolean isCurrentUserSuperAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }
}