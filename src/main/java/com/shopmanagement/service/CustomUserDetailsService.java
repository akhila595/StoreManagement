package com.shopmanagement.service;

import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    // ==========================================================
    // Load user by email (used during login)
    // ==========================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return buildUserDetails(user);
    }

    // ==========================================================
    // Load user by ID (used after decoding JWT)
    // ==========================================================
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for ID: " + userId));

        return buildUserDetails(user);
    }

    // ==========================================================
    // Build Spring UserDetails object from our User entity
    // ==========================================================
    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(r -> "ROLE_" + r.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),        // username
                user.getPassword(),     // password
                authorities             // roles/authorities
        );
    }
}
