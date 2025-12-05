package com.shopmanagement.controller;

import com.shopmanagement.dto.PermissionDTO;
import com.shopmanagement.model.Permission;
import com.shopmanagement.service.PermissionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    // Get all permissions
    @GetMapping
    public List<PermissionDTO> list() {
        return permissionService.getAll().stream()
                .map(this::convertToDTO)  // Convert from Permission entity to PermissionDTO
                .collect(Collectors.toList());
    }

    // Create a new permission
    @PostMapping
    public PermissionDTO create(@RequestBody PermissionDTO permissionDTO) {
        Permission permission = convertToEntity(permissionDTO);  // Convert DTO to entity
        permission.setId(null);  // Ensure auto-generation of ID
        Permission createdPermission = permissionService.create(permission);
        return convertToDTO(createdPermission);  // Convert the created entity back to DTO
    }

    // Update an existing permission
    @PutMapping("/{id}")
    public PermissionDTO update(@PathVariable("id") Long id, @RequestBody PermissionDTO permissionDTO) {
        permissionDTO.setId(id);  // Ensure correct ID is set in DTO
        Permission permission = convertToEntity(permissionDTO);  // Convert DTO to entity
        Permission updatedPermission = permissionService.update(id, permission);
        return convertToDTO(updatedPermission);  // Convert the updated entity back to DTO
    }

    // Delete a permission
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        permissionService.delete(id);
    }

    // Convert Permission entity to PermissionDTO
    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setCode(permission.getCode());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }

    // Convert PermissionDTO to Permission entity
    private Permission convertToEntity(PermissionDTO dto) {
        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setCode(dto.getCode());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        return permission;
    }
}
