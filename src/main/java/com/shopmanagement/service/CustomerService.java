package com.shopmanagement.service;

import java.util.List;
import java.util.Optional;
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

    // ==========================================================
    // CREATE CUSTOMER + ADMIN USER
    // ==========================================================
    @Transactional
    public CustomerDTO createCustomerWithAdmin(CustomerRegistrationRequest request) {
        // 1️⃣ Create new customer
        Customer customer = new Customer();
        customer.setCustomerName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());
        customer = customerRepository.saveAndFlush(customer);

        // 2️⃣ Ensure global ADMIN role template exists
        Role adminTemplate = roleRepository.findByNameAndCustomerIsNull("ADMIN").orElse(null);
        if (adminTemplate == null) {
            adminTemplate = new Role();
            adminTemplate.setName("ADMIN");
            adminTemplate.setDescription("Global ADMIN role template");
            adminTemplate.setCustomer(null);
            adminTemplate.setPermissions(new java.util.HashSet<>(permissionRepository.findAll()));
            adminTemplate = roleRepository.save(adminTemplate);
        }

        // 3️⃣ Create ADMIN role for this customer
        Role customerAdminRole = new Role();
        customerAdminRole.setName("ADMIN");
        customerAdminRole.setDescription("Admin role for customer " + customer.getCustomerName());
        customerAdminRole.setPermissions(new java.util.HashSet<>(adminTemplate.getPermissions()));
        customerAdminRole.setCustomer(customer);
        roleRepository.save(customerAdminRole);

        // 4️⃣ Create admin user for this customer
        User adminUser = new User();
        adminUser.setName(request.getAdminName());
        adminUser.setEmail(request.getAdminEmail());
        adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        adminUser.setCustomer(customer);
        adminUser.setRoles(Set.of(customerAdminRole));
        userRepository.save(adminUser);

        // ✅ Return DTO
        return mapToDTO(customer);
    }

    // ==========================================================
    // GET ALL CUSTOMERS
    // ==========================================================
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // GET CUSTOMER BY ID
    // ==========================================================
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // ==========================================================
    // UPDATE CUSTOMER
    // ==========================================================
    public CustomerDTO updateCustomer(Long id, Customer updated) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setCustomerName(updated.getCustomerName());
        customer.setEmail(updated.getEmail());
        customer.setPhone(updated.getPhone());
        customer.setAddress(updated.getAddress());
        customer.setGstNumber(updated.getGstNumber());

        customer = customerRepository.save(customer);
        return mapToDTO(customer);
    }

    // ==========================================================
    // DELETE CUSTOMER + RELATED USERS & ROLES
    // ==========================================================
    @Transactional
    public void deleteCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);

        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer with ID " + id + " not found.");
        }

        Customer customer = customerOpt.get();

        // 1️⃣ Delete all users linked to this customer
        userRepository.deleteAll(userRepository.findByCustomer_Id(id));

        // 2️⃣ Delete all roles linked to this customer
        roleRepository.deleteAll(roleRepository.findByCustomer_Id(id));

        // 3️⃣ Delete the customer itself
        customerRepository.deleteById(id);

        System.out.println("🗑️ Customer " + customer.getCustomerName() + " deleted successfully.");
    }

    // ==========================================================
    // ENTITY → DTO MAPPING
    // ==========================================================
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
