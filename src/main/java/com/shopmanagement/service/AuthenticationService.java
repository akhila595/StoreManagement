package com.shopmanagement.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.UserRepository;
import com.shopmanagement.repository.RoleRepository;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {

		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public String register(String email, String password, String name) {

		if (userRepository.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("Email already registered");
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setName(name);

		Role defaultRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException("Default role USER missing in DB."));

		user.setRoles(Set.of(defaultRole));

		userRepository.save(user);

		return "User registered successfully!";
	}

	public Map<String, Object> authenticate(String email, String password) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		String token = jwtUtil.generateToken(user.getEmail());

		// 🔥 Collect unique permission CODES
		Set<String> permissionCodes = new HashSet<>();

		user.getRoles()
				.forEach(role -> role.getPermissions().forEach(permission -> permissionCodes.add(permission.getCode()) // return
																														// CODE
																														// only
				));

		return Map.of("token", token, "name", user.getName(), "email", user.getEmail(), "roles", user.getRoleNames(), // returns
																														// role
																														// names
				"permissions", permissionCodes // now returning permission CODES
		);
	}
}
