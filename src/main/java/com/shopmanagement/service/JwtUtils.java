package com.shopmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopmanagement.service.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    // ✅ Unified customerId resolver (SAFE)
    public Long getCustomerIdFromToken() {

        Object customerAttr = request.getAttribute("customerId");
        if (customerAttr instanceof Long) {
            return (Long) customerAttr;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.extractCustomerId(token);
        }

        return null;
    }

    public Long getUserIdFromToken() {
        Object userAttr = request.getAttribute("userId");
        if (userAttr instanceof Long) {
            return (Long) userAttr;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.extractUserId(token);
        }

        return null;
    }

    public boolean isCurrentUserSuperAdmin() {
        Object flag = request.getAttribute("isSuperAdmin");
        return flag instanceof Boolean && (Boolean) flag;
    }
    
    public Long getRequiredCustomerId() {
    	 Long customerId = getCustomerIdFromToken();
        if (customerId == null) {
            throw new RuntimeException("Please select a customer");
        }
        return customerId;
    }
}
