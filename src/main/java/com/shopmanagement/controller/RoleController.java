package com.shopmanagement.controller;

import com.shopmanagement.dto.AssignRolesRequest;
import com.shopmanagement.dto.RoleDTO;
import com.shopmanagement.dto.AssignPermissionsRequest;
import com.shopmanagement.model.Permission;
import com.shopmanagement.model.Role;
import com.shopmanagement.service.RoleService;
import com.shopmanagement.service.UserService;
import com.shopmanagement.service.PermissionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final UserService userService;

    public RoleController(RoleService roleService,
                          PermissionService permissionService,
                          UserService userService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.userService = userService;
    }

    // ================================
    // ROLE CRUD
    // ================================
    @GetMapping
    public ResponseEntity<List<RoleDTO>> listRoles() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable("id") Long id, @RequestBody RoleDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    // ✅ Safe delete with proper error handling
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete: role is assigned to users. Unassign before deleting.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete role: " + e.getMessage());
        }
    }

    // ================================
    // PERMISSIONS
    // ================================
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(permissionService.getAll());
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission p) {
        p.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(p));
    }

    @PutMapping("/permissions/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable("id") Long id,
                                                       @RequestBody Permission p) {
        p.setId(id);
        return ResponseEntity.ok(permissionService.update(id, p));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ================================
    // USER ROLE ASSIGNMENT
    // ================================
    @PostMapping("/assign")
    public ResponseEntity<Void> assignRoles(@RequestBody AssignRolesRequest request) {
        roleService.assignRoles(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Role>> getRolesForUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(roleService.getRolesForUser(userId));
    }

    // ================================
    // GET / ASSIGN PERMISSIONS
    // ================================
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<Permission>> getPermissionsForRole(@PathVariable("roleId") Long roleId) {
        return ResponseEntity.ok(roleService.getPermissionsForRole(roleId));
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<Void> assignPermissionsToRole(
            @PathVariable("roleId") Long roleId,
            @RequestBody AssignPermissionsRequest request) {

        roleService.assignPermissionsToRole(roleId, request.getPermissionIds());
        return ResponseEntity.ok().build();
    }
}
