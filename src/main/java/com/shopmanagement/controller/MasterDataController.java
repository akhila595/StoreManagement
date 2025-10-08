package com.shopmanagement.controller;

import com.shopmanagement.model.*;
import com.shopmanagement.service.MasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")  // Added versioning to API
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    // -------- Categories --------
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return masterDataService.getAllCategories();
    }

    @PostMapping("/categories/create")
    public Category createCategory(@RequestBody Category category) {
        return masterDataService.addCategory(category);  // renamed to `createCategory`
    }

    // -------- Brands --------
    @GetMapping("/brands")
    public List<Brand> getAllBrands() {
        return masterDataService.getAllBrands();
    }

    @PostMapping("/brands/create")
    public Brand createBrand(@RequestBody Brand brand) {
        return masterDataService.addBrand(brand);  // renamed to `createBrand`
    }

    // -------- Cloth Types --------
    @GetMapping("/cloth-types")
    public List<ClothType> getAllClothTypes() {
        return masterDataService.getAllClothTypes();
    }

    @PostMapping("/cloth-types/create")
    public ClothType createClothType(@RequestBody ClothType clothType) {
        return masterDataService.addClothType(clothType);  // renamed to `createClothType`
    }

    // -------- Colors --------
    @GetMapping("/colors")
    public List<Color> getAllColors() {
        return masterDataService.getAllColors();
    }

    @PostMapping("/colors/create")
    public Color createColor(@RequestBody Color color) {
        return masterDataService.addColor(color);  // renamed to `createColor`
    }

    // -------- Sizes --------
    @GetMapping("/sizes")
    public List<Size> getAllSizes() {
        return masterDataService.getAllSizes();
    }

    @PostMapping("/sizes/create")
    public Size createSize(@RequestBody Size size) {
        return masterDataService.addSize(size);  // renamed to `createSize`
    }
}
