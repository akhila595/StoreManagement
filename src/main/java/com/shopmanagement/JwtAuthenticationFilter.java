package com.shopmanagement;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shopmanagement.model.User;
import com.shopmanagement.repository.UserRepository;
import com.shopmanagement.service.CustomUserDetailsService;
import com.shopmanagement.service.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        if (jwt != null && jwtUtil.validateToken(jwt)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            Long userId = jwtUtil.extractUserId(jwt);
            Long tokenCustomerId = jwtUtil.extractCustomerId(jwt);

            if (userId == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
                return;
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found");
                return;
            }

            boolean isSuperAdmin = user.getRoles().stream()
                    .anyMatch(r ->
                            r.getName().equalsIgnoreCase("SUPERADMIN") ||
                            r.getName().equalsIgnoreCase("SUPER_ADMIN"));

            Long resolvedCustomerId = tokenCustomerId;

            // ✅ Superadmin: customerId must come from header
            if (isSuperAdmin) {
                String headerCustomerId = request.getHeader("X-Customer-Id");
                if (headerCustomerId != null && !headerCustomerId.isBlank()) {
                    resolvedCustomerId = Long.valueOf(headerCustomerId);
                }
            }

            // ❌ Non-superadmin must have customerId in token
            if (!isSuperAdmin && resolvedCustomerId == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Customer context missing");
                return;
            }

            // ✅ Store context
            request.setAttribute("userId", userId);
            request.setAttribute("customerId", resolvedCustomerId);
            request.setAttribute("isSuperAdmin", isSuperAdmin);

            UserDetails userDetails = userDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/forgot-password")
                || path.startsWith("/public/")
                || path.equals("/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
