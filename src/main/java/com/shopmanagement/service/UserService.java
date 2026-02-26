package com.shopmanagement.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public UserService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       CustomerRepository customerRepo,
                       JwtUtils jwtUtils) {
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
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
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
    public UserDTO create(UserDTO dto) {

        boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
        Long customerId = jwtUtils.getCustomerId();

        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus("ACTIVE");

        // Assign roles safely
        if (dto.getRoleNames() != null && !dto.getRoleNames().isEmpty()) {

            Set<Role> roles = dto.getRoleNames().stream()
                    .map(name -> {
                        if (isSuperAdmin)
                            return roleRepo.findByName(name)
                                    .orElseThrow(() ->
                                            new RuntimeException("Role not found: " + name));

                        return roleRepo.findByNameAndCustomer_IdAndStatus(
                                        name, customerId, "ACTIVE")
                                .orElseThrow(() ->
                                        new RuntimeException("Role not found: " + name));
                    })
                    .collect(Collectors.toSet());

            user.setRoles(roles);
        }

        // Assign customer (skip for SuperAdmin)
        if (!isSuperAdmin) {
            if (customerId == null)
                throw new RuntimeException("Customer context missing");

            Customer customer = customerRepo.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            user.setCustomer(customer);
        }

        userRepo.save(user);
        return toDTO(user);
    }

    // ==========================================================
    // UPDATE USER
    // ==========================================================
    @Transactional
    public UserDTO update(Long id, UserDTO dto) {

        boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
        Long customerId = jwtUtils.getCustomerId();

        User user;

        if (isSuperAdmin) {
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            if (customerId == null)
                throw new RuntimeException("Unauthorized");

            user = userRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
        }

        // Name
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        // Email uniqueness check
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {

            userRepo.findByEmail(dto.getEmail())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new RuntimeException("Email already exists");
                    });

            user.setEmail(dto.getEmail());
        }

        // Password
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Roles (optional update)
        if (dto.getRoleNames() != null) {

            Set<Role> roles = dto.getRoleNames().isEmpty()
                    ? Set.of()
                    : dto.getRoleNames().stream()
                            .map(name -> {
                                if (isSuperAdmin)
                                    return roleRepo.findByName(name)
                                            .orElseThrow(() ->
                                                    new RuntimeException("Role not found: " + name));

                                return roleRepo.findByNameAndCustomer_IdAndStatus(
                                                name, customerId, "ACTIVE")
                                        .orElseThrow(() ->
                                                new RuntimeException("Role not found: " + name));
                            })
                            .collect(Collectors.toSet());

            user.setRoles(roles);
        }

        // Profile image
        if (dto.getProfileImage() != null) {
            user.setProfileImage(dto.getProfileImage());
        }

        userRepo.save(user);
        return toDTO(user);
    }

    // ==========================================================
    // SOFT DELETE USER
    // ==========================================================
    @Transactional
    public void delete(Long id) {

        boolean isSuperAdmin = jwtUtils.isCurrentUserSuperAdmin();
        Long customerId = jwtUtils.getCustomerId();

        User user;

        if (isSuperAdmin) {
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
        }

        user.setStatus("DELETED");
        userRepo.save(user);
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

        dto.setRoleNames(
                u.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

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