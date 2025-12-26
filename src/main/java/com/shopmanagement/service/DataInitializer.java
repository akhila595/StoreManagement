package com.shopmanagement.service;

import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BrandRepository brandRepository;
    private final ClothTypeRepository clothTypeRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;

    public DataInitializer(RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           BrandRepository brandRepository,
                           ClothTypeRepository clothTypeRepository,
                           SizeRepository sizeRepository,
                           ColorRepository colorRepository,
                           CategoryRepository categoryRepository,
                           CustomerRepository customerRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.brandRepository = brandRepository;
        this.clothTypeRepository = clothTypeRepository;
        this.sizeRepository = sizeRepository;
        this.colorRepository = colorRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("🚀 Starting database initialization check...");

        seedPermissionsIfEmpty();
        seedRolesIfEmpty();
        seedBrandsIfEmpty();
        seedClothTypesIfEmpty();
        seedSizesIfEmpty();
        seedColorsIfEmpty();
        seedCategoriesIfEmpty();

        System.out.println("✅ Database initialization completed safely.");
    }

    // =====================================================
    // PERMISSIONS
    // =====================================================
    private void seedPermissionsIfEmpty() {
        if (permissionRepository.count() > 0) {
            System.out.println("➡️ Permissions already exist, skipping.");
            return;
        }

        List<Permission> required = Arrays.asList(
            new Permission("PRODUCT_VIEW", "View Products", "Can view product listings"),
            new Permission("PRODUCT_ADD", "Add Products", "Can add new products"),
            new Permission("PRODUCT_EDIT", "Edit Products", "Can edit existing products"),
            new Permission("PRODUCT_DELETE", "Delete Products", "Can delete products"),
            new Permission("REPORT_VIEW", "View Reports", "Can view all reports"),
            new Permission("INVENTORY_VIEW", "View Inventory", "Can view inventory levels"),
            new Permission("INVENTORY_MANAGE", "Manage Inventory", "Can perform stock operations"),
            new Permission("BRAND_MANAGE", "Manage Brands", "Can manage brands"),
            new Permission("CATEGORY_MANAGE", "Manage Categories", "Can manage categories"),
            new Permission("SIZE_MANAGE", "Manage Sizes", "Can manage sizes"),
            new Permission("COLOR_MANAGE", "Manage Colors", "Can manage colors"),
            new Permission("CLOTH_TYPE_MANAGE", "Manage Cloth Types", "Can manage cloth types"),
            new Permission("USER_VIEW", "View Users", "Can view user accounts"),
            new Permission("USER_CREATE", "Create Users", "Can create new users"),
            new Permission("USER_EDIT", "Edit Users", "Can edit user details"),
            new Permission("USER_DELETE", "Delete Users", "Can delete users"),
            new Permission("ROLE_VIEW", "View Roles", "Can view roles"),
            new Permission("ROLE_CREATE", "Create Roles", "Can create new roles"),
            new Permission("ROLE_EDIT", "Edit Roles", "Can edit roles"),
            new Permission("ROLE_DELETE", "Delete Roles", "Can delete roles"),
            new Permission("PERMISSION_MANAGE", "Manage Permissions", "Can edit permissions"),
            new Permission("SYSTEM_SETTINGS_EDIT", "Edit System Settings", "Can modify system settings"),
            new Permission("VIEW_LOGS", "View Logs", "Can view system logs"),
            new Permission("STOCK_OUT_MANAGE", "Stock Out Manage", "Can access stock-out page"),
            new Permission("STOCK_IN_MANAGE", "Stock In Manage", "Can access stock-in page"),
            new Permission("LOW_STOCK_VIEW", "Low Stock View", "Can view low stock details"),
            new Permission("SUPPLIER_VIEW", "View Supplier", "Can view supplier information"),
            new Permission("SUPPLIER_CREATE", "Create Supplier", "Can create suppliers"),
            new Permission("SUPPLIER_EDIT", "Edit Supplier", "Can edit supplier details"),
            new Permission("SUPPLIER_DELETE", "Delete Supplier", "Can delete suppliers"),
            new Permission("ROLE_ASSIGN", "Assign Role", "Can assign roles to users"),
            new Permission("ROLE_MANAGE", "Manage Roles", "Full role management")
        );

        permissionRepository.saveAll(required);
        System.out.println("✅ Permissions table seeded successfully.");
    }

    // =====================================================
    // ROLES
    // =====================================================
    private void seedRolesIfEmpty() {
        if (roleRepository.count() > 0) {
            System.out.println("➡️ Roles already exist, skipping.");
            return;
        }

        System.out.println("🆕 Seeding default global roles...");

        // 🔹 Create a default customer (for initial roles only)
        Customer defaultCustomer = customerRepository.findByCustomerName("Default Customer")
            .orElseGet(() -> {
                Customer c = new Customer();
                c.setCustomerName("Default Customer");
                c.setEmail("default@example.com");
                c.setPhone("9999999999");
                return customerRepository.save(c);
            });

        // 🔹 Global roles (no customer)
        Role superAdmin = new Role("SUPERADMIN", "Full system-wide access");
        superAdmin.setPermissions(new HashSet<>(permissionRepository.findAll()));
        superAdmin.setCustomer(null);
        roleRepository.save(superAdmin);

        // 🔹 Default tenant roles (for demo)
        Role admin = new Role("ADMIN", "Administrator access");
        admin.setCustomer(defaultCustomer);
        admin.setPermissions(new HashSet<>(permissionRepository.findAll()));
        roleRepository.save(admin);

        Role manager = new Role("MANAGER", "Manager role");
        manager.setCustomer(defaultCustomer);
        manager.setPermissions(new HashSet<>(Arrays.asList(find("PRODUCT_VIEW"), find("REPORT_VIEW"))));
        roleRepository.save(manager);

        Role user = new Role("USER", "Standard user");
        user.setCustomer(defaultCustomer);
        user.setPermissions(new HashSet<>(Arrays.asList(find("PRODUCT_VIEW"), find("INVENTORY_VIEW"))));
        roleRepository.save(user);

        System.out.println("✅ Roles table seeded successfully.");
    }

    // =====================================================
    // OTHER STATIC DATA
    // =====================================================
    private void seedBrandsIfEmpty() {
        if (brandRepository.count() > 0) return;
        List<String> brands = Arrays.asList("Nike","Adidas","Puma","Levi’s","H&M","Zara","Reebok");
        brands.forEach(b -> { if (!brandRepository.existsByBrand(b)) brandRepository.save(new Brand(b)); });
    }

    private void seedClothTypesIfEmpty() {
        if (clothTypeRepository.count() > 0) return;
        List<String> types = Arrays.asList("Shirt","T-Shirt","Jeans","Jacket","Kurta","Saree");
        types.forEach(t -> { if (!clothTypeRepository.existsByClothType(t)) clothTypeRepository.save(new ClothType(t)); });
    }

    private void seedSizesIfEmpty() {
        if (sizeRepository.count() > 0) return;
        List<String> sizes = Arrays.asList("S","M","L","XL","XXL");
        sizes.forEach(s -> { if (!sizeRepository.existsBySize(s)) sizeRepository.save(new Size(s)); });
    }

    private void seedColorsIfEmpty() {
        if (colorRepository.count() > 0) return;
        List<String> colors = Arrays.asList("Red","Blue","Black","White","Green");
        colors.forEach(c -> { if (!colorRepository.existsByColor(c)) colorRepository.save(new Color(c)); });
    }

    private void seedCategoriesIfEmpty() {
        if (categoryRepository.count() > 0) return;
        List<String> cats = Arrays.asList("Men","Women","Kids","Accessories","Footwear");
        cats.forEach(c -> { if (!categoryRepository.existsByCategoryName(c)) categoryRepository.save(new Category(c)); });
    }

    private Permission find(String code) {
        return permissionRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Missing permission: " + code));
    }
}
