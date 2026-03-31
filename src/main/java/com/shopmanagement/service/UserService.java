package com.shopmanagement.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.dto.UserDTO;
import com.shopmanagement.model.Customer;
import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.CustomerRepository;
import com.shopmanagement.repository.RoleRepository;
import com.shopmanagement.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;
	private final CustomerRepository customerRepo;
	private final JwtUtils jwtUtils;

	public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder,
			CustomerRepository customerRepo, JwtUtils jwtUtils) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
		this.customerRepo = customerRepo;
		this.jwtUtils = jwtUtils;
	}

	// ==========================================================
	// GET ALL USERS
	// ==========================================================
	public List<UserDTO> getAll() {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		List<User> users;

		if (isSuperAdmin) {
			users = userRepo.findByStatus("ACTIVE");
		} else {
			if (customerId == null)
				throw new RuntimeException("Unauthorized");

			users = userRepo.findByCustomer_IdAndStatus(customerId, "ACTIVE");
		}

		return users.stream().map(this::toDTO).toList();
	}

	// ==========================================================
	// GET USER BY ID
	// ==========================================================
	public UserDTO getById(Long id) {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		User user;

		if (isSuperAdmin) {
			user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		} else {
			if (customerId == null)
				throw new RuntimeException("Unauthorized");

			user = userRepo.findByIdAndCustomer_Id(id, customerId)
					.orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
		}

		return toDTO(user);
	}

	// ==========================================================
	// CREATE USER
	// ==========================================================
	@Transactional
	public ResponseEntity<?> create(UserDTO dto) {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		// Check email exists
		if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
		}

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setName(dto.getName());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setStatus("ACTIVE");

		// Assign roles safely
		if (dto.getRoleNames() != null && !dto.getRoleNames().isEmpty()) {

			Set<Role> roles = new HashSet<>();

			for (String name : dto.getRoleNames()) {

				Optional<Role> role;

				if (isSuperAdmin) {
					role = roleRepo.findByName(name);
				} else {
					role = roleRepo.findByNameAndCustomer_IdAndStatus(name, customerId, "ACTIVE");
				}

				if (role.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found: " + name);
				}

				roles.add(role.get());
			}

			user.setRoles(roles);
		}

		// Assign customer
		if (!isSuperAdmin) {

			if (customerId == null) {
				return ResponseEntity.badRequest().body("Customer context missing");
			}

			Optional<Customer> customer = customerRepo.findById(customerId);

			if (customer.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
			}

			user.setCustomer(customer.get());
		}

		userRepo.save(user);

		return ResponseEntity.ok(toDTO(user));
	}

	// ==========================================================
	// UPDATE USER
	// ==========================================================
	@Transactional
	public ResponseEntity<?> update(Long id, UserDTO dto) {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		User user;

		// Find user
		if (isSuperAdmin) {
			Optional<User> optionalUser = userRepo.findById(id);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			user = optionalUser.get();
		} else {

			if (customerId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
			}

			Optional<User> optionalUser = userRepo.findByIdAndCustomer_Id(id, customerId);

			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or unauthorized");
			}

			user = optionalUser.get();
		}

		// Name update
		if (dto.getName() != null && !dto.getName().isBlank()) {
			user.setName(dto.getName());
		}

		// Email uniqueness check
		if (dto.getEmail() != null && !dto.getEmail().isBlank()) {

			Optional<User> existingUser = userRepo.findByEmail(dto.getEmail());

			if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
			}

			user.setEmail(dto.getEmail());
		}

		// Password update
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		// Roles update
		if (dto.getRoleNames() != null) {

			Set<Role> roles = new HashSet<>();

			for (String name : dto.getRoleNames()) {

				Optional<Role> role;

				if (isSuperAdmin) {
					role = roleRepo.findByName(name);
				} else {
					role = roleRepo.findByNameAndCustomer_IdAndStatus(name, customerId, "ACTIVE");
				}

				if (role.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found: " + name);
				}

				roles.add(role.get());
			}

			user.setRoles(roles);
		}

		// Profile image
		if (dto.getProfileImage() != null) {
			user.setProfileImage(dto.getProfileImage());
		}

		userRepo.save(user);

		return ResponseEntity.ok(toDTO(user));
	}

	// ==========================================================
	// SOFT DELETE USER
	// ==========================================================
	@Transactional
	public ResponseEntity<?> delete(Long id) {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		User user;

		if (isSuperAdmin) {

			Optional<User> optionalUser = userRepo.findById(id);

			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}

			user = optionalUser.get();

		} else {

			Optional<User> optionalUser = userRepo.findByIdAndCustomer_Id(id, customerId);

			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or unauthorized");
			}

			user = optionalUser.get();
		}

		user.setStatus("DELETED");
		userRepo.save(user);

		return ResponseEntity.ok("User deleted successfully");
	}

	// ==========================================================
	// MAPPING TO DTO
	// ==========================================================
	private UserDTO toDTO(User u) {

		UserDTO dto = new UserDTO();
		dto.setId(u.getId());
		dto.setEmail(u.getEmail());
		dto.setName(u.getName());
		dto.setProfileImage(u.getProfileImage());

		dto.setRoleNames(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

		if (u.getCustomer() != null)
			dto.setCustomerId(u.getCustomer().getId());

		return dto;
	}

	// ==========================================================
	// GET USERS FOR CURRENT CUSTOMER
	// ==========================================================
	public List<UserDTO> getUsersWithRolesForCurrentCustomer() {

		boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
		Long customerId = jwtUtils.getCustomerId();

		List<User> users;

		if (isSuperAdmin) {
			users = userRepo.findByStatus("ACTIVE");
		} else {
			if (customerId == null)
				throw new RuntimeException("Unauthorized");

			users = userRepo.findByCustomer_IdAndStatus(customerId, "ACTIVE");
		}

		return users.stream().map(this::toDTO).toList();
	}
}