package com.shopmanagement.service;

import com.shopmanagement.model.Permission;
import com.shopmanagement.repository.PermissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository repo;

    public PermissionService(PermissionRepository repo) {
        this.repo = repo;
    }

    public List<Permission> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Permission create(Permission p) {
        return repo.save(p);
    }

    @Transactional
    public Permission update(Long id, Permission p) {

        Permission existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (p.getCode() != null)
            existing.setCode(p.getCode());

        if (p.getName() != null)
            existing.setName(p.getName());

        if (p.getDescription() != null)
            existing.setDescription(p.getDescription());

        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
