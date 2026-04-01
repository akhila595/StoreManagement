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
    // GET ALL ROLES
    // ==========================================================
    public List<RoleDTO> getAll() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Role> roles = roleRepo.findByCustomer_Id(customerId);

        return roles.stream().map(this::toDTO).toList();
    }

    // ==========================================================
    // GET ROLE BY ID
    // ==========================================================
    public RoleDTO getById(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = roleRepo
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found or unauthorized"));

        return toDTO(role);
    }

    // ==========================================================
    // CREATE ROLE
    // ==========================================================
    @Transactional
    public RoleDTO create(RoleDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setCustomer(customer);

        role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        return toDTO(roleRepo.save(role));
    }

    // ==========================================================
    // UPDATE ROLE
    // ==========================================================
    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = roleRepo
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found or unauthorized"));

        if (dto.getName() != null)
            role.setName(dto.getName());

        if (dto.getDescription() != null)
            role.setDescription(dto.getDescription());

        if (dto.getPermissionIds() != null)
            role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        return toDTO(roleRepo.save(role));
    }

    // ==========================================================
    // DELETE ROLE
    // ==========================================================
    @Transactional
    public void delete(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = roleRepo
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found or unauthorized"));

        // Prevent deletion if role assigned to users
        boolean roleAssigned = userRepo.findAll()
                .stream()
                .anyMatch(u -> u.getRoles()
                        .stream()
                        .anyMatch(r -> Objects.equals(r.getId(), id)));

        if (roleAssigned) {
            throw new DataIntegrityViolationException(
                    "Cannot delete: role is assigned to users");
        }

        roleRepo.delete(role);
    }

    // ==========================================================
    // ASSIGN ROLES TO USER
    // ==========================================================
    @Transactional
    public void assignRoles(AssignRolesRequest req) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        User user = userRepo
                .findByIdAndCustomer_Id(req.getUserId(), customerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found or unauthorized"));

        Set<Role> roles = new HashSet<>();

        for (Long roleId : req.getRoleIds()) {

            Role role = roleRepo
                    .findByIdAndCustomer_Id(roleId, customerId)
                    .orElseThrow(() ->
                            new RuntimeException("Role not found or unauthorized"));

            roles.add(role);
        }

        user.setRoles(roles);
        userRepo.save(user);
    }

    // ==========================================================
    // GET ROLES FOR USER
    // ==========================================================
    public List<Role> getRolesForUser(Long userId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        User user = userRepo
                .findByIdAndCustomer_Id(userId, customerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found or unauthorized"));

        return new ArrayList<>(user.getRoles());
    }

    // ==========================================================
    // GET PERMISSIONS FOR ROLE
    // ==========================================================
    public List<Permission> getPermissionsForRole(Long roleId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = roleRepo
                .findByIdAndCustomer_Id(roleId, customerId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found or unauthorized"));

        return new ArrayList<>(role.getPermissions());
    }

    // ==========================================================
    // ASSIGN PERMISSIONS TO ROLE
    // ==========================================================
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Role role = roleRepo
                .findByIdAndCustomer_Id(roleId, customerId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found or unauthorized"));

        Set<Permission> permissions =
                new HashSet<>(permRepo.findAllById(permissionIds));

        role.setPermissions(permissions);

        roleRepo.save(role);
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private Set<Permission> fetchPermissions(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return new HashSet<>();
        return new HashSet<>(permRepo.findAllById(ids));
    }

    private RoleDTO toDTO(Role r) {

        RoleDTO dto = new RoleDTO();

        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());

        dto.setPermissionIds(
                r.getPermissions()
                        .stream()
                        .map(Permission::getId)
                        .toList()
        );

        if (r.getCustomer() != null)
            dto.setCustomerId(r.getCustomer().getId());

        return dto;
    }
}