package com.shopmanagement.controller;

import com.shopmanagement.service.UserService;
import com.shopmanagement.service.RoleService;
import com.shopmanagement.service.PermissionService;
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

    public DashboardStatsController(
            UserService userService,
            RoleService roleService,
            PermissionService permissionService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userService.getAll().size());
        stats.put("totalRoles", roleService.getAll().size());
        stats.put("totalPermissions", permissionService.getAll().size());

        // If you have logs in DB, replace with logsService.count()
        stats.put("totalLogs", 0);

        return stats;
    }
}
