package com.shopmanagement.service;

import com.shopmanagement.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserPrincipal implements UserDetails {

    private Long userId;
    private Long customerId;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(User user,
                               Collection<? extends GrantedAuthority> authorities) {

        this.userId = user.getId();

        if (user.getCustomer() != null) {
            this.customerId = user.getCustomer().getId();
        }

        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCustomerId() {
        return customerId;
    }

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