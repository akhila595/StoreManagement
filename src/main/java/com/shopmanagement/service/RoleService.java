package com.shopmanagement.service;

import com.shopmanagement.dto.AssignRolesRequest;
import com.shopmanagement.dto.RoleDTO;
import com.shopmanagement.model.Permission;
import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.PermissionRepository;
import com.shopmanagement.repository.RoleRepository;
import com.shopmanagement.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;
    private final UserRepository userRepo;

    public RoleService(RoleRepository roleRepo,
                       PermissionRepository permRepo,
                       UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.permRepo = permRepo;
        this.userRepo = userRepo;
    }

    // =========================
    // ROLE CRUD
    // =========================
    public List<RoleDTO> getAll() {
        return roleRepo.findAll().stream().map(this::toDTO).toList();
    }

    public RoleDTO getById(Long id) {
        return roleRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public RoleDTO create(RoleDTO dto) {
        Role r = new Role();
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setPermissions(fetchPermissions(dto.getPermissionIds()));
        return toDTO(roleRepo.save(r));
    }

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {
        Role r = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setPermissions(fetchPermissions(dto.getPermissionIds()));
        return toDTO(roleRepo.save(r));
    }

    @Transactional
    public void delete(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // ✅ Check if any users are assigned to this role
        List<User> usersWithRole = userRepo.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> Objects.equals(r.getId(), id)))
                .toList();

        if (!usersWithRole.isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Cannot delete: role is assigned to users");
        }

        roleRepo.delete(role);
    }

    // =========================
    // USER → ROLE ASSIGNMENT
    // =========================
    @Transactional
    public void assignRoles(AssignRolesRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = new HashSet<>(roleRepo.findAllById(req.getRoleIds()));
        user.setRoles(roles);
        userRepo.save(user);
    }

    public List<Role> getRolesForUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getRoles());
    }

    // =========================
    // ROLE → PERMISSIONS ASSIGNMENT
    // =========================
    public List<Permission> getPermissionsForRole(Long roleId) {
        Role r = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return new ArrayList<>(r.getPermissions());
    }

    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> permissions = new HashSet<>(permRepo.findAllById(permissionIds));
        role.setPermissions(permissions);
        roleRepo.save(role);
    }

    // =========================
    // HELPERS
    // =========================
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
        return dto;
    }
}
