package com.shopmanagement.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopmanagement.dto.UserDTO;
import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.RoleRepository;
import com.shopmanagement.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
	}

	// ==========================================================
	// GET ALL USERS
	// ==========================================================
	public List<UserDTO> getAll() {
		return userRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ==========================================================
	// GET USER BY ID
	// ==========================================================
	public UserDTO getById(Long id) {
		User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		return toDTO(user);
	}

	// ==========================================================
	// CREATE USER (SUPER_ADMIN creates users)
	// ==========================================================
	public UserDTO create(UserDTO dto) {

		if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setName(dto.getName());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		// Assign roles using names instead of IDs
		Set<Role> roles = dto.getRoleNames() == null || dto.getRoleNames().isEmpty() ? Set.of()
				: dto.getRoleNames().stream().map(roleRepo::findByName)
						.map(r -> r.orElseThrow(() -> new RuntimeException("Role not found")))
						.collect(Collectors.toSet());

		user.setRoles(roles);

		userRepo.save(user);
		return toDTO(user);
	}

	// ==========================================================
	// UPDATE USER
	// ==========================================================
	public UserDTO update(Long id, UserDTO dto) {

		User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		user.setName(dto.getName());
		user.setEmail(dto.getEmail());

		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		// Update roles using names
		Set<Role> roles = dto.getRoleNames() == null || dto.getRoleNames().isEmpty() ? Set.of()
				: dto.getRoleNames().stream().map(roleRepo::findByName)
						.map(r -> r.orElseThrow(() -> new RuntimeException("Role not found")))
						.collect(Collectors.toSet());

		user.setRoles(roles);

		return toDTO(userRepo.save(user));
	}

	// ==========================================================
	// DELETE USER
	// ==========================================================
	public void delete(Long id) {
		if (!userRepo.existsById(id)) {
			throw new RuntimeException("User not found");
		}
		userRepo.deleteById(id);
	}

	// ==========================================================
	// MAPPING TO DTO
	// ==========================================================
	private UserDTO toDTO(User u) {
		UserDTO dto = new UserDTO();
		dto.setId(u.getId());
		dto.setEmail(u.getEmail());
		dto.setName(u.getName());
		dto.setRoleNames(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
		return dto;
	}
}
