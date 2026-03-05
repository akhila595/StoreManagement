package com.shopmanagement.controller;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.dto.AttributeResponseDTO;
import com.shopmanagement.service.AttributeService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /* ================= CREATE ================= */

    @PostMapping
    public Map<String, Object> create(@RequestBody AttributeDTO dto) {
        return attributeService.createAttribute(dto);
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public List<AttributeResponseDTO> getAll() {
        return attributeService.getAllAttributes();
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody AttributeDTO dto) {

        return attributeService.updateAttribute(id, dto);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return attributeService.deleteAttribute(id);
    }
}