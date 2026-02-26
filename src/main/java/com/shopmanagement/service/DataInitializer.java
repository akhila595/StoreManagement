package com.shopmanagement.service;

import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("🚀 Starting system bootstrap...");

        seedPermissionsIfMissing();
        seedSuperAdminRole();
        seedSuperAdminUser();

        System.out.println("✅ System bootstrap completed.");
    }

    // =====================================================
    // 1️⃣ GLOBAL PERMISSIONS
    // =====================================================

    private void seedPermissionsIfMissing() {

        List<String> permissionCodes = List.of(
                "PRODUCT_VIEW",
                "PRODUCT_ADD",
                "PRODUCT_EDIT",
                "PRODUCT_DELETE",
                "REPORT_VIEW",
                "INVENTORY_VIEW",
                "INVENTORY_MANAGE",
                "STOCK_IN_MANAGE",
                "STOCK_OUT_MANAGE",
                "LOW_STOCK_VIEW",
                "SUPPLIER_VIEW",
                "SUPPLIER_CREATE",
                "SUPPLIER_EDIT",
                "SUPPLIER_DELETE",
                "USER_VIEW",
                "USER_CREATE",
                "USER_EDIT",
                "USER_DELETE",
                "ROLE_VIEW",
                "ROLE_CREATE",
                "ROLE_EDIT",
                "ROLE_DELETE",
                "ROLE_ASSIGN",
                "PERMISSION_MANAGE",
                "SYSTEM_SETTINGS_EDIT",
                "VIEW_LOGS"
        );

        for (String code : permissionCodes) {

            permissionRepository.findByCode(code)
                    .orElseGet(() -> {
                        Permission permission = new Permission();
                        permission.setCode(code);
                        permission.setName(code.replace("_", " "));
                        permission.setDescription(code + " permission");
                        return permissionRepository.save(permission);
                    });
        }

        System.out.println("✅ Permissions verified.");
    }

    // =====================================================
    // 2️⃣ GLOBAL SUPERADMIN ROLE
    // =====================================================

    private void seedSuperAdminRole() {

        Role superAdminRole = roleRepository
                .findByNameAndCustomerIsNull("SUPERADMIN")
                .orElseGet(() -> {

                    System.out.println("🆕 Creating SUPERADMIN role...");

                    Role role = new Role();
                    role.setName("SUPERADMIN");
                    role.setDescription("Full system-wide access");
                    role.setCustomer(null); // GLOBAL ROLE
                    role.setPermissions(new HashSet<>(permissionRepository.findAll()));

                    return roleRepository.save(role);
                });

        System.out.println("✅ SUPERADMIN role verified.");
    }

    // =====================================================
    // 3️⃣ GLOBAL SUPERADMIN USER
    // =====================================================

    private void seedSuperAdminUser() {

        final String EMAIL = "superadmin@system.com";

        if (userRepository.existsByEmail(EMAIL)) {
            System.out.println("➡️ SUPERADMIN user already exists.");
            return;
        }

        System.out.println("🛡️ Creating SUPERADMIN user...");

        Role superAdminRole = roleRepository
                .findByNameAndCustomerIsNull("SUPERADMIN")
                .orElseThrow(() -> new RuntimeException("SUPERADMIN role missing"));

        User user = new User();
        user.setEmail(EMAIL);
        user.setName("System Super Admin");
        user.setPassword(passwordEncoder.encode("ChangeMe@123"));
        user.setCustomer(null); // GLOBAL USER
        user.setRoles(Set.of(superAdminRole));

        userRepository.save(user);

        System.out.println("✅ SUPERADMIN user created.");
    }
}