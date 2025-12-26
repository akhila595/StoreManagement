package com.shopmanagement.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.model.Customer;
import com.shopmanagement.repository.UserRepository;
import com.shopmanagement.repository.RoleRepository;
import com.shopmanagement.repository.CustomerRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ==========================================================
    // 🧩 REGISTER (for first-time setup)
    // ==========================================================
    public String register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);

        // ✅ If SUPERADMIN role doesn't exist yet, create it on the fly
        Role superAdmin = roleRepository.findByName("SUPERADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SUPERADMIN");
                    role.setDescription("Highest-level access (system-wide)");
                    // 🚫 No customer required for SuperAdmin
                    return roleRepository.save(role);
                });

        user.setRoles(Set.of(superAdmin));

        // 🚫 Do not assign customer for SUPERADMIN
        userRepository.save(user);

        return "SuperAdmin registered successfully!";
    }

    // ==========================================================
    // 🔐 LOGIN / AUTHENTICATE
    // ==========================================================
    public Map<String, Object> authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Long customerId = (user.getCustomer() != null) ? user.getCustomer().getId() : null;

        // ✅ Generate token with only userId and customerId
        String token = jwtUtil.generateToken(user.getId(), customerId);

        // ✅ Collect permissions safely (user may not have any yet)
        Set<String> permissionCodes = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> permissionCodes.add(p.getCode()));
                }
            });
        }

        // ✅ Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("customerId", customerId);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("roles", (user.getRoles() != null)
                ? user.getRoleNames()
                : Set.of("SUPERADMIN")); // default fallback
        response.put("permissions", permissionCodes);

        return response;
    }
}
