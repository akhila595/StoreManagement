package com.shopmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopmanagement.dto.UserDTO;
import com.shopmanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){ this.userService = userService; }

    @GetMapping
    public List<UserDTO> list(){
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable Long id){
        return userService.getById(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto){
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable("id") Long id, @RequestBody UserDTO dto){
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        userService.delete(id);
    }
    
    @GetMapping("/users-with-roles")
    public ResponseEntity<List<UserDTO>> getUsersWithRoles() {
        return ResponseEntity.ok(
                userService.getUsersWithRolesForCurrentCustomer()
        );
    }

}
