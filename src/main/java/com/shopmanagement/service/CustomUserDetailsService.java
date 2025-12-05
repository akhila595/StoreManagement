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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // 🔥 Convert roles → Spring authorities
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(Role::getName)                  // SUPER_ADMIN
                .map(r -> "ROLE_" + r)              // ROLE_SUPER_ADMIN
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities   // 🔥 ROLES HERE
        );
    }
}
