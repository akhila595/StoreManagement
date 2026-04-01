package com.shopmanagement.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

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

        // 🔹 1️⃣ First check request attribute (set by JWT filter)
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object customerId = request.getAttribute("customerId");

            if (customerId instanceof Long) {
                return (Long) customerId;
            }
        }

        // 🔹 2️⃣ Fallback to authentication principal
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