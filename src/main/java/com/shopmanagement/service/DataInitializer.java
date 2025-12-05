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

    public DataInitializer(RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           BrandRepository brandRepository,
                           ClothTypeRepository clothTypeRepository,
                           SizeRepository sizeRepository,
                           ColorRepository colorRepository,
                           CategoryRepository categoryRepository) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.brandRepository = brandRepository;
        this.clothTypeRepository = clothTypeRepository;
        this.sizeRepository = sizeRepository;
        this.colorRepository = colorRepository;
        this.categoryRepository = categoryRepository;
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
        long count = permissionRepository.count();
        if (count > 0) {
            System.out.println("➡️ Permissions already exist (" + count + "), skipping seeding.");
            return;
        }

        System.out.println("🆕 Seeding default permissions...");

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
        long count = roleRepository.count();
        if (count > 0) {
            System.out.println("➡️ Roles already exist (" + count + "), skipping seeding.");
            return;
        }

        System.out.println("🆕 Seeding default roles...");

        Role superAdmin = new Role("SUPER_ADMIN", "Highest access with full control");
        superAdmin.setPermissions(new HashSet<>(permissionRepository.findAll()));
        roleRepository.save(superAdmin);

        Role admin = new Role("ADMIN", "Administrator access");
        admin.setPermissions(new HashSet<>(Arrays.asList(
            find("PRODUCT_VIEW"), find("PRODUCT_ADD"), find("PRODUCT_EDIT"), find("PRODUCT_DELETE"),
            find("INVENTORY_MANAGE"), find("BRAND_MANAGE"), find("CATEGORY_MANAGE"),
            find("SIZE_MANAGE"), find("COLOR_MANAGE"), find("CLOTH_TYPE_MANAGE"),
            find("REPORT_VIEW"), find("USER_VIEW"), find("USER_CREATE"), find("USER_EDIT"),
            find("USER_DELETE"), find("ROLE_VIEW"), find("ROLE_CREATE"), find("ROLE_EDIT"),
            find("ROLE_DELETE"), find("PERMISSION_MANAGE"), find("SYSTEM_SETTINGS_EDIT"), find("VIEW_LOGS")
        )));
        roleRepository.save(admin);

        Role manager = new Role("MANAGER", "Manager role");
        manager.setPermissions(new HashSet<>(Arrays.asList(
            find("PRODUCT_VIEW"), find("PRODUCT_ADD"), find("PRODUCT_EDIT"),
            find("REPORT_VIEW"), find("INVENTORY_MANAGE")
        )));
        roleRepository.save(manager);

        Role user = new Role("USER", "Standard user");
        user.setPermissions(new HashSet<>(Arrays.asList(
            find("PRODUCT_VIEW"), find("INVENTORY_VIEW"), find("REPORT_VIEW")
        )));
        roleRepository.save(user);

        System.out.println("✅ Roles table seeded successfully.");
    }

    // =====================================================
    // BRANDS
    // =====================================================
    private void seedBrandsIfEmpty() {
        if (brandRepository.count() > 0) {
            System.out.println("➡️ Brands already exist, skipping seeding.");
            return;
        }

        List<String> list = Arrays.asList(
            "Nike", "Adidas", "Puma", "Levi’s", "H&M", "Zara", "Reebok",
            "Uniqlo", "Tommy Hilfiger", "Under Armour", "Roadster", "U.S. Polo Assn"
        );

        for (String name : list) {
            if (!brandRepository.existsByBrand(name)) {
                brandRepository.save(new Brand(name));
            }
        }
        System.out.println("✅ Brands table seeded successfully.");
    }

    // =====================================================
    private void seedClothTypesIfEmpty() {
        if (clothTypeRepository.count() > 0) {
            System.out.println("➡️ Cloth types already exist, skipping seeding.");
            return;
        }

        List<String> list = Arrays.asList(
            "Shirt", "T-Shirt", "Jeans", "Jacket", "Kurta", "Saree", "Shorts",
            "Tracksuit", "Hoodie", "Blazer"
        );

        for (String name : list) {
            if (!clothTypeRepository.existsByClothType(name)) {
                clothTypeRepository.save(new ClothType(name));
            }
        }
        System.out.println("✅ Cloth types table seeded successfully.");
    }

    // =====================================================
    private void seedSizesIfEmpty() {
        if (sizeRepository.count() > 0) {
            System.out.println("➡️ Sizes already exist, skipping seeding.");
            return;
        }

        List<String> sizes = Arrays.asList("XS", "S", "M", "L", "XL", "XXL", "3XL", "Free Size");

        for (String s : sizes) {
            if (!sizeRepository.existsBySize(s)) {
                sizeRepository.save(new Size(s));
            }
        }
        System.out.println("✅ Sizes table seeded successfully.");
    }

    // =====================================================
    private void seedColorsIfEmpty() {
        if (colorRepository.count() > 0) {
            System.out.println("➡️ Colors already exist, skipping seeding.");
            return;
        }

        List<String> colors = Arrays.asList(
            "Red", "Blue", "Black", "White", "Green", "Yellow", "Pink", "Purple",
            "Gray", "Orange", "Brown", "Beige"
        );

        for (String c : colors) {
            if (!colorRepository.existsByColor(c)) {
                colorRepository.save(new Color(c));
            }
        }
        System.out.println("✅ Colors table seeded successfully.");
    }

    // =====================================================
    private void seedCategoriesIfEmpty() {
        if (categoryRepository.count() > 0) {
            System.out.println("➡️ Categories already exist, skipping seeding.");
            return;
        }

        List<String> list = Arrays.asList(
            "Men", "Women", "Kids", "Accessories", "Sportswear", "Footwear",
            "Winter Collection", "Ethnic Wear"
        );

        for (String name : list) {
            if (!categoryRepository.existsByCategoryName(name)) {
                categoryRepository.save(new Category(name));
            }
        }
        System.out.println("✅ Categories table seeded successfully.");
    }

    // =====================================================
    private Permission find(String code) {
        return permissionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Missing permission: " + code));
    }
}
