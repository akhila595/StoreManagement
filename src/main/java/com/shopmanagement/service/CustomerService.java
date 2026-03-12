package com.shopmanagement.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.dto.CustomerDTO;
import com.shopmanagement.dto.CustomerRegistrationRequest;
import com.shopmanagement.model.Customer;
import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.CustomerRepository;
import com.shopmanagement.repository.PermissionRepository;
import com.shopmanagement.repository.RoleRepository;
import com.shopmanagement.repository.UserRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;

    public CustomerService(CustomerRepository customerRepository,
                           RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PermissionRepository permissionRepository) {

        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
    }

    /* ==========================================================
       CREATE CUSTOMER + TENANT ADMIN USER
       ========================================================== */

    @Transactional
    public CustomerDTO createCustomerWithAdmin(CustomerRegistrationRequest request) {

        // 1️⃣ Prevent duplicate customer email
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer email already exists.");
        }

        // 2️⃣ Prevent duplicate admin email
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new RuntimeException("Admin email already exists.");
        }

        // 3️⃣ Create Customer
        Customer customer = new Customer();
        customer.setCustomerName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());
        customer.setStatus("ACTIVE");

        // saveAndFlush ensures ID is generated immediately
        customer = customerRepository.saveAndFlush(customer);

     // Create final reference for lambda usage
        final Customer savedCustomer = customer;
        
     // 4️⃣ Create or get ADMIN role safely
        Role adminRole = roleRepository
                .findByNameAndCustomer_IdAndStatus("ADMIN", savedCustomer.getId(), "ACTIVE")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("Admin role for " + savedCustomer.getCustomerName());
                    role.setCustomer(savedCustomer);
                    role.setPermissions(new HashSet<>(permissionRepository.findAll()));
                    role.setStatus("ACTIVE");
                    return roleRepository.save(role);
                });
        
        // 5️⃣ Create Admin User
        User adminUser = new User();
        adminUser.setName(request.getAdminName());
        adminUser.setEmail(request.getAdminEmail());
        adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        adminUser.setCustomer(customer);
        adminUser.setRoles(Set.of(adminRole));
        adminUser.setStatus("ACTIVE");

        userRepository.save(adminUser);

        return mapToDTO(customer);
    }

    /* ==========================================================
       GET ALL CUSTOMERS (ACTIVE ONLY)
       ========================================================== */

    public List<CustomerDTO> getAllCustomers() {

        return customerRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ==========================================================
       GET CUSTOMER BY ID
       ========================================================== */

    public CustomerDTO getCustomerById(Long id) {

        Customer customer = customerRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToDTO(customer);
    }

    /* ==========================================================
       UPDATE CUSTOMER
       ========================================================== */
    @Transactional
    public CustomerDTO updateCustomer(Long id, Customer updated) {

        Customer customer = customerRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (updated.getEmail() != null &&
                !customer.getEmail().equals(updated.getEmail()) &&
                customerRepository.existsByEmail(updated.getEmail())) {

            throw new RuntimeException("Email already in use.");
        }

        if (updated.getCustomerName() != null)
            customer.setCustomerName(updated.getCustomerName());

        if (updated.getEmail() != null)
            customer.setEmail(updated.getEmail());

        if (updated.getPhone() != null)
            customer.setPhone(updated.getPhone());

        if (updated.getAddress() != null)
            customer.setAddress(updated.getAddress());

        if (updated.getGstNumber() != null)
            customer.setGstNumber(updated.getGstNumber());

        customer = customerRepository.save(customer);

        return mapToDTO(customer);
    }

    /* ==========================================================
       SOFT DELETE CUSTOMER (Safe)
       ========================================================== */

    @Transactional
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Optional protection for system tenant
        if (customer.getId() == 1L) {
            throw new RuntimeException("System customer cannot be deleted.");
        }

        // 1️⃣ Soft delete customer
        customer.setStatus("DELETED");
        customerRepository.save(customer);

        // 2️⃣ Soft delete users
        List<User> users = userRepository.findByCustomer_Id(id);
        users.forEach(u -> u.setStatus("DELETED"));
        userRepository.saveAll(users);

        // 3️⃣ Soft delete roles
        List<Role> roles = roleRepository.findByCustomer_Id(id);
        roles.forEach(r -> r.setStatus("DELETED"));
        roleRepository.saveAll(roles);

        System.out.println("🗑️ Customer soft deleted safely.");
    }

    /* ==========================================================
       ENTITY → DTO
       ========================================================== */

    private CustomerDTO mapToDTO(Customer customer) {

        return new CustomerDTO(
                customer.getId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getGstNumber()
        );
    }
}