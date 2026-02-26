package com.shopmanagement.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}") // milliseconds
    private long expiration;

    @Value("${jwt.issuer:shopmanagement-app}")
    private String issuer;

    private Key getSigningKey() {

        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ==========================================================
    // 🔐 GENERATE TOKEN
    // ==========================================================
    public String generateToken(Long userId, Long customerId) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId);

        return Jwts.builder()
                .setSubject(userId.toString()) // 🔥 official subject
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==========================================================
    // 🔍 EXTRACT USER ID
    // ==========================================================
    public Long extractUserId(String token) {

        Claims claims = parseToken(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    // ==========================================================
    // 🔍 EXTRACT CUSTOMER ID
    // ==========================================================
    public Long extractCustomerId(String token) {

        Object val = parseToken(token).getBody().get("customerId");
        return val != null ? Long.valueOf(val.toString()) : null;
    }

    // ==========================================================
    // ✅ VALIDATE TOKEN
    // ==========================================================
    public boolean validateToken(String token) {

        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT");
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT");
        } catch (SignatureException e) {
            System.out.println("Invalid signature");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid token");
        }

        return false;
    }

    // ==========================================================
    // 🔐 INTERNAL PARSER
    // ==========================================================
    private Jws<Claims> parseToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(30) // 🔥 allow 30 sec drift
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }
}