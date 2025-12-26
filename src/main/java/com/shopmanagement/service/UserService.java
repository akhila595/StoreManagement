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
    // GET ALL USERS (Scoped by Customer if not SuperAdmin)
    // ==========================================================
    public List<UserDTO> getAll() {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<User> users;
        if (customerId == null) {
            // 🟩 SuperAdmin → See all users
            users = userRepo.findAll();
        } else {
            users = userRepo.findByCustomer_Id(customerId);
        }

        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ==========================================================
    // GET USER BY ID
    // ==========================================================
    public UserDTO getById(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        User user;
        if (customerId == null) {
            // 🟩 SuperAdmin
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("User not found or unauthorized access"));
        }

        return toDTO(user);
    }

    // ==========================================================
    // CREATE USER
    // ==========================================================
    @Transactional
    public UserDTO create(UserDTO dto) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // ✅ Assign roles (for both SuperAdmin and Customer Admin)
        Set<Role> roles = dto.getRoleNames() == null || dto.getRoleNames().isEmpty()
                ? Set.of()
                : dto.getRoleNames().stream()
                        .map(name -> roleRepo.findByName(name)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + name)))
                        .collect(Collectors.toSet());
        user.setRoles(roles);

        // ✅ Assign customer (skip if SuperAdmin)
        if (customerId != null) {
            Customer customer = customerRepo.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            user.setCustomer(customer);
        } else {
            user.setCustomer(null); // SuperAdmin creating user globally
        }

        userRepo.save(user);
        return toDTO(user);
    }

    // ==========================================================
    // UPDATE USER
    // ==========================================================
    @Transactional
    public UserDTO update(Long id, UserDTO dto) {

        Long customerId = jwtUtils.getCustomerIdFromToken(); // ⚠️ NOT required

        User user;

        // 🔐 Superadmin → can update any user
        if (jwtUtils.isCurrentUserSuperAdmin()) {
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        // 🔐 Admin → restricted to own customer
        else {
            if (customerId == null) {
                throw new RuntimeException("Customer context missing");
            }

            user = userRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
        }

        /* ==========================
           SAFE FIELD UPDATES
        ========================== */

        // ✅ Name (optional)
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        // ⚠️ Email update (optional – keep if your UI allows)
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        // ✅ Password (optional)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        /* ==========================
           ROLES — SAFE HANDLING
           (ONLY update if provided)
        ========================== */
        if (dto.getRoleNames() != null) {

            Set<Role> roles = dto.getRoleNames().isEmpty()
                    ? Set.of() // explicit role clear if UI sends empty list
                    : dto.getRoleNames().stream()
                            .map(name -> roleRepo.findByName(name)
                                    .orElseThrow(() ->
                                            new RuntimeException("Role not found: " + name)))
                            .collect(Collectors.toSet());

            user.setRoles(roles);
        }
        // 🚫 If roleNames == null → DO NOTHING (keep existing roles)

        userRepo.save(user);
        return toDTO(user);
    }


    // ==========================================================
    // DELETE USER
    // ==========================================================
    public void delete(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        User user;
        if (customerId == null) {
            user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
        }

        userRepo.delete(user);
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
        if (u.getCustomer() != null)
            dto.setCustomerId(u.getCustomer().getId());
        return dto;
    }
    
    public List<UserDTO> getUsersWithRolesForCurrentCustomer() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<User> users;

        // 🟩 SuperAdmin → all users
        if (customerId == null) {
            users = userRepo.findAll();
        }
        // 🟦 Customer Admin → only own customer users
        else {
            users = userRepo.findByCustomer_Id(customerId);
        }

        return users.stream().map(this::toDTO).toList();
    }

}
