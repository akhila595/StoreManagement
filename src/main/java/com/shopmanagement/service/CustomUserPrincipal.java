package com.shopmanagement.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserPrincipal implements UserDetails {

    private final Long userId;
    private final Long customerId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(Long userId,
                               Long customerId,
                               String email,
                               String password,
                               Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.customerId = customerId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    // ===============================
    // UserDetails methods
    // ===============================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}