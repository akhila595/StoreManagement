package com.shopmanagement.service;

import com.shopmanagement.dto.AssignRolesRequest;
import com.shopmanagement.dto.RoleDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final JwtUtils jwtUtils;

    public RoleService(RoleRepository roleRepo,
                       PermissionRepository permRepo,
                       UserRepository userRepo,
                       CustomerRepository customerRepo,
                       JwtUtils jwtUtils) {
        this.roleRepo = roleRepo;
        this.permRepo = permRepo;
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.jwtUtils = jwtUtils;
    }

    // ==========================================================
    // ROLE CRUD (supports SuperAdmin + Customer Admin)
    // ==========================================================
    public List<RoleDTO> getAll() {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Role> roles;
        if (customerId == null) {
            // 🟩 SuperAdmin — can view all roles
            roles = roleRepo.findAll();
        } else {
            roles = roleRepo.findByCustomer_Id(customerId);
        }

        return roles.stream().map(this::toDTO).toList();
    }

    public RoleDTO getById(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role;
        if (customerId == null) {
            // 🟩 SuperAdmin — can access any role
            role = roleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = roleRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("Role not found or unauthorized access"));
        }

        return toDTO(role);
    }

    @Transactional
    public RoleDTO create(RoleDTO dto) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        // ✅ Assign customer if not SuperAdmin
        if (customerId != null) {
            Customer customer = customerRepo.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            role.setCustomer(customer);
        } else {
            role.setCustomer(null); // SuperAdmin global role
        }

        return toDTO(roleRepo.save(role));
    }

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role;
        if (customerId == null) {
            role = roleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = roleRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("Role not found or unauthorized access"));
        }

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        return toDTO(roleRepo.save(role));
    }

    @Transactional
    public void delete(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role;
        if (customerId == null) {
            role = roleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = roleRepo.findByIdAndCustomer_Id(id, customerId)
                    .orElseThrow(() -> new RuntimeException("Role not found or unauthorized access"));
        }

        // ✅ Prevent deletion if assigned to users
        List<User> usersWithRole = userRepo.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> Objects.equals(r.getId(), id)))
                .toList();

        if (!usersWithRole.isEmpty()) {
            throw new DataIntegrityViolationException("Cannot delete: role is assigned to users");
        }

        roleRepo.delete(role);
    }

    // ==========================================================
    // USER → ROLE ASSIGNMENT
    // ==========================================================
    @Transactional
    public void assignRoles(AssignRolesRequest req) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🟩 SuperAdmin can assign roles globally
        if (customerId != null) {
            if (user.getCustomer() == null || !Objects.equals(user.getCustomer().getId(), customerId)) {
                throw new RuntimeException("Unauthorized: cannot assign roles across customers");
            }
        }

        Set<Role> roles = new HashSet<>();
        for (Long roleId : req.getRoleIds()) {
            Role role;
            if (customerId == null) {
                role = roleRepo.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
            } else {
                role = roleRepo.findByIdAndCustomer_Id(roleId, customerId)
                        .orElseThrow(() -> new RuntimeException("Role not found or unauthorized"));
            }
            roles.add(role);
        }

        user.setRoles(roles);
        userRepo.save(user);
    }

    // ==========================================================
    // USER → GET ROLES
    // ==========================================================
    public List<Role> getRolesForUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getRoles());
    }

    // ==========================================================
    // ROLE → PERMISSIONS ASSIGNMENT
    // ==========================================================
    public List<Permission> getPermissionsForRole(Long roleId) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role;
        if (customerId == null) {
            role = roleRepo.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = roleRepo.findByIdAndCustomer_Id(roleId, customerId)
                    .orElseThrow(() -> new RuntimeException("Role not found or unauthorized access"));
        }

        return new ArrayList<>(role.getPermissions());
    }

    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role;
        if (customerId == null) {
            role = roleRepo.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = roleRepo.findByIdAndCustomer_Id(roleId, customerId)
                    .orElseThrow(() -> new RuntimeException("Role not found or unauthorized access"));
        }

        Set<Permission> permissions = new HashSet<>(permRepo.findAllById(permissionIds));
        role.setPermissions(permissions);
        roleRepo.save(role);
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private Set<Permission> fetchPermissions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(permRepo.findAllById(ids));
    }

    private RoleDTO toDTO(Role r) {
        RoleDTO dto = new RoleDTO();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setPermissionIds(
                r.getPermissions().stream().map(Permission::getId).toList()
        );
        if (r.getCustomer() != null)
            dto.setCustomerId(r.getCustomer().getId());
        return dto;
    }
}
