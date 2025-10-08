package com.shopmanagement.service;

import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MasterDataService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ClothTypeRepository clothTypeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    // -------- Categories --------
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // -------- Brands --------
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // -------- Cloth Types --------
    public List<ClothType> getAllClothTypes() {
        return clothTypeRepository.findAll();
    }

    public ClothType addClothType(ClothType clothType) {
        return clothTypeRepository.save(clothType);
    }

    // -------- Colors --------
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color addColor(Color color) {
        return colorRepository.save(color);
    }

    // -------- Sizes --------
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size addSize(Size size) {
        return sizeRepository.save(size);
    }
}
