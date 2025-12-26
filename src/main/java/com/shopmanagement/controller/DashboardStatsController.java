package com.shopmanagement.controller;

import com.shopmanagement.service.UserService;
import com.shopmanagement.service.RoleService;
import com.shopmanagement.service.PermissionService;
import com.shopmanagement.service.JwtUtils;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardStatsController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final JwtUtils jwtUtils;

    public DashboardStatsController(
            UserService userService,
            RoleService roleService,
            PermissionService permissionService,
            JwtUtils jwtUtils
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userService.getAll().size());
        stats.put("totalRoles", roleService.getAll().size());
        stats.put("totalPermissions", permissionService.getAll().size());

        // Logs intentionally disabled
        stats.put("totalLogs", 0);

        return stats;
    }
}
