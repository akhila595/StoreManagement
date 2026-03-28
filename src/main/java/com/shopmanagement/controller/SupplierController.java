package com.shopmanagement.controller;

import com.shopmanagement.dto.SupplierDTO;
import com.shopmanagement.model.Supplier;
import com.shopmanagement.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<SupplierDTO> getAll() {
        return supplierService.getAllSuppliers();
    }

    @PostMapping
    public SupplierDTO  createSupplier(@RequestBody SupplierDTO dto) {
        return supplierService.saveOrUpdate(dto);
    }

    @PutMapping("/{id}")
    public SupplierDTO  updateSupplier(@PathVariable("id") Long id, @RequestBody SupplierDTO dto) {
        dto.setSupplierId(id);
        return supplierService.saveOrUpdate(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable("id") Long id) {
        supplierService.deleteSupplier(id);
    }
}
