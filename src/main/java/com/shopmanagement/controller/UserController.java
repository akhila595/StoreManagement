package com.shopmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.UserDTO;
import com.shopmanagement.service.ImageStorageService;
import com.shopmanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final ImageStorageService imageStorageService;

    public UserController(UserService userService,
                          ImageStorageService imageStorageService) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
    }

    // ===============================
    // GET ALL USERS
    // ===============================
    @GetMapping
    public List<UserDTO> list() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable(name = "id") Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return userService.create(dto);
    }

    // ===============================
    // UPDATE USER (SEND IMAGE URL)
    // ===============================
    @PutMapping("/{id}")
    public UserDTO update(@PathVariable(name = "id") Long id, @RequestBody UserDTO dto) {
        return userService.update(id, dto);
    }

    // ===============================
    // DELETE USER
    // ===============================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
    }

    // ===============================
    // USERS WITH ROLES
    // ===============================
    @GetMapping("/users-with-roles")
    public ResponseEntity<List<UserDTO>> getUsersWithRoles() {
        return ResponseEntity.ok(
                userService.getUsersWithRolesForCurrentCustomer()
        );
    }

    // ==================================================
    // ✅ UPLOAD USER PROFILE IMAGE (NEW)
    // ==================================================
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable(name = "id") Long id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String imageUrl = imageStorageService.storeUserProfileImage(id, file);

        // UI will use this URL in update API
        return ResponseEntity.ok(
                Map.of("imageUrl", imageUrl)
        );
    }
}
