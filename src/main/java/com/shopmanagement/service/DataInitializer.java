package com.shopmanagement.service;

import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("🚀 Starting system bootstrap...");

        seedPermissionsIfMissing();
        seedSuperAdminRole();
        seedSuperAdminUser();

        Customer customer = seedDefaultCustomer();

        seedCustomerDefaultRoles(customer);
        seedCustomerAdminUser(customer);

        System.out.println("✅ System bootstrap completed.");
    }

    // =====================================================
    // GLOBAL PERMISSIONS
    // =====================================================

    private void seedPermissionsIfMissing() {

        List<Permission> required = Arrays.asList(

                new Permission("PRODUCT_VIEW","View Products","Can view product listings"),
                new Permission("PRODUCT_ADD","Add Products","Can add new products"),
                new Permission("PRODUCT_EDIT","Edit Products","Can edit existing products"),
                new Permission("PRODUCT_DELETE","Delete Products","Can delete products"),

                new Permission("REPORT_VIEW","View Reports","Can view all reports"),

                new Permission("INVENTORY_VIEW","View Inventory","Can view inventory levels"),
                new Permission("INVENTORY_MANAGE","Manage Inventory","Can perform stock operations"),

                new Permission("BRAND_MANAGE","Manage Brands","Can manage brands"),
                new Permission("CATEGORY_MANAGE","Manage Categories","Can manage categories"),
                new Permission("SIZE_MANAGE","Manage Sizes","Can manage sizes"),
                new Permission("COLOR_MANAGE","Manage Colors","Can manage colors"),
                new Permission("CLOTH_TYPE_MANAGE","Manage Cloth Types","Can manage cloth types"),

                new Permission("USER_VIEW","View Users","Can view user accounts"),
                new Permission("USER_CREATE","Create Users","Can create new users"),
                new Permission("USER_EDIT","Edit Users","Can edit user details"),
                new Permission("USER_DELETE","Delete Users","Can delete users"),

                new Permission("ROLE_VIEW","View Roles","Can view roles"),
                new Permission("ROLE_CREATE","Create Roles","Can create new roles"),
                new Permission("ROLE_EDIT","Edit Roles","Can edit roles"),
                new Permission("ROLE_DELETE","Delete Roles","Can delete roles"),
                new Permission("ROLE_ASSIGN","Assign Role","Can assign roles to users"),
                new Permission("ROLE_MANAGE","Manage Roles","Full role management"),

                new Permission("PERMISSION_MANAGE","Manage Permissions","Can edit permissions"),
                new Permission("SYSTEM_SETTINGS_EDIT","Edit System Settings","Can modify system settings"),
                new Permission("VIEW_LOGS","View Logs","Can view system logs"),

                new Permission("STOCK_OUT_MANAGE","Stock Out Manage","Can access stock-out page"),
                new Permission("STOCK_IN_MANAGE","Stock In Manage","Can access stock-in page"),
                new Permission("LOW_STOCK_VIEW","Low Stock View","Can view low stock details"),

                new Permission("SUPPLIER_VIEW","View Supplier","Can view supplier information"),
                new Permission("SUPPLIER_CREATE","Create Supplier","Can create suppliers"),
                new Permission("SUPPLIER_EDIT","Edit Supplier","Can edit supplier details"),
                new Permission("SUPPLIER_DELETE","Delete Supplier","Can delete suppliers")
        );

        for (Permission p : required) {

            permissionRepository.findByCode(p.getCode())
                    .orElseGet(() -> permissionRepository.save(p));
        }

        System.out.println("✅ Permissions verified.");
    }

    // =====================================================
    // SUPERADMIN ROLE
    // =====================================================

    private void seedSuperAdminRole() {

        roleRepository.findByNameAndCustomerIsNull("SUPERADMIN")
                .orElseGet(() -> {

                    System.out.println("🆕 Creating SUPERADMIN role...");

                    Role role = new Role();
                    role.setName("SUPERADMIN");
                    role.setDescription("Full system-wide access");
                    role.setCustomer(null);
                    role.setPermissions(new HashSet<>(permissionRepository.findAll()));

                    return roleRepository.save(role);
                });

        System.out.println("✅ SUPERADMIN role verified.");
    }

    // =====================================================
    // SUPERADMIN USER
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
        user.setCustomer(null);
        user.setRoles(Set.of(superAdminRole));

        userRepository.save(user);

        System.out.println("✅ SUPERADMIN user created.");
    }

    // =====================================================
    // DEFAULT CUSTOMER
    // =====================================================

    private Customer seedDefaultCustomer() {

        final String CUSTOMER_NAME = "Demo Customer";

        Optional<Customer> existing = customerRepository.findAll()
                .stream()
                .filter(c -> CUSTOMER_NAME.equals(c.getCustomerName()))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        System.out.println("🆕 Creating default customer...");

        Customer customer = new Customer();
        customer.setCustomerName(CUSTOMER_NAME);
        customer.setEmail("demo@customer.com");
        customer.setPhone("9999999999");
        customer.setAddress("Demo Address");
        customer.setStatus("ACTIVE");

        return customerRepository.save(customer);
    }

    // =====================================================
    // CUSTOMER DEFAULT ROLES
    // =====================================================

    private void seedCustomerDefaultRoles(Customer customer) {

        Map<String, Set<String>> rolePermissions = getDefaultRolePermissions();

        for (String roleName : rolePermissions.keySet()) {

            Optional<Role> existing = roleRepository
                    .findByNameAndCustomer_IdAndStatus(roleName, customer.getId(), "ACTIVE");

            if (existing.isPresent()) continue;

            Role role = new Role();
            role.setName(roleName);
            role.setCustomer(customer);
            role.setStatus("ACTIVE");

            Set<Permission> permissions = permissionRepository.findAll()
                    .stream()
                    .filter(p -> rolePermissions.get(roleName).contains(p.getCode()))
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);

            roleRepository.save(role);

            System.out.println("✅ Created role: " + roleName);
        }
    }

    // =====================================================
    // ROLE PERMISSION MAP
    // =====================================================

    private Map<String, Set<String>> getDefaultRolePermissions() {

        Map<String, Set<String>> roles = new HashMap<>();

        roles.put("ADMIN",
                permissionRepository.findAll()
                        .stream()
                        .map(Permission::getCode)
                        .collect(Collectors.toSet())
        );

        roles.put("MANAGER", Set.of(
                "PRODUCT_VIEW","PRODUCT_ADD","PRODUCT_EDIT",
                "INVENTORY_VIEW","INVENTORY_MANAGE",
                "STOCK_IN_MANAGE","STOCK_OUT_MANAGE",
                "LOW_STOCK_VIEW",
                "SUPPLIER_VIEW","SUPPLIER_CREATE","SUPPLIER_EDIT",
                "BRAND_MANAGE","CATEGORY_MANAGE",
                "REPORT_VIEW"
        ));

        roles.put("STAFF", Set.of(
                "PRODUCT_VIEW",
                "INVENTORY_VIEW",
                "STOCK_IN_MANAGE",
                "STOCK_OUT_MANAGE",
                "LOW_STOCK_VIEW",
                "SUPPLIER_VIEW"
        ));

        return roles;
    }

    // =====================================================
    // DEFAULT CUSTOMER ADMIN USER
    // =====================================================

    private void seedCustomerAdminUser(Customer customer) {

        final String EMAIL = "admin@demo.com";

        if (userRepository.findByEmail(EMAIL).isPresent()) {
            return;
        }

        System.out.println("🆕 Creating CUSTOMER ADMIN user...");

        Role adminRole = roleRepository
                .findByNameAndCustomer_IdAndStatus("ADMIN", customer.getId(), "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        User user = new User();
        user.setEmail(EMAIL);
        user.setName("Demo Admin");
        user.setPassword(passwordEncoder.encode("Admin@123"));
        user.setCustomer(customer);
        user.setStatus("ACTIVE");
        user.setRoles(Set.of(adminRole));

        userRepository.save(user);
    }
}