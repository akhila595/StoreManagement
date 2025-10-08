package com.shopmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopmanagement.model.User;
import com.shopmanagement.repository.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    // ✅ Register new user
    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash password
        userRepository.save(user);

        return "User registered successfully!";
    }

    // ✅ Authenticate user and generate JWT
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail()); // Return JWT token
        //
    }
}
