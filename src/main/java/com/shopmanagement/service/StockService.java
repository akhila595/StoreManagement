package com.shopmanagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.StockInRequestDTO;
import com.shopmanagement.dto.StockOutRequestDTO;
import com.shopmanagement.model.Brand;
import com.shopmanagement.model.Category;
import com.shopmanagement.model.ClothType;
import com.shopmanagement.model.Color;
import com.shopmanagement.model.Product;
import com.shopmanagement.model.ProductVariant;
import com.shopmanagement.model.Purchase;
import com.shopmanagement.model.SaleInvoice;
import com.shopmanagement.model.SaleItem;
import com.shopmanagement.model.Size;
import com.shopmanagement.model.StockMovement;
import com.shopmanagement.model.Supplier;
import com.shopmanagement.repository.BrandRepository;
import com.shopmanagement.repository.CategoryRepository;
import com.shopmanagement.repository.ClothTypeRepository;
import com.shopmanagement.repository.ColorRepository;
import com.shopmanagement.repository.ProductRepository;
import com.shopmanagement.repository.ProductVariantRepository;
import com.shopmanagement.repository.PurchaseRepository;
import com.shopmanagement.repository.SaleInvoiceRepository;
import com.shopmanagement.repository.SaleItemRepository;
import com.shopmanagement.repository.SizeRepository;
import com.shopmanagement.repository.StockMovementRepository;
import com.shopmanagement.repository.SupplierRepository;
import com.shopmanagement.dto.RecentStockInDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class StockService {

	@Autowired
	private CategoryRepository categoryRepo;
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private ProductVariantRepository variantRepo;
	@Autowired
	private SupplierRepository supplierRepo;
	@Autowired
	private PurchaseRepository purchaseRepo;
	@Autowired
	private StockMovementRepository movementRepo;
	@Autowired
	private SaleItemRepository saleItemRepo;
	@Autowired
	private SaleInvoiceRepository saleInvoiceRepo;
	@Autowired
	private BrandRepository brandRepo;
	@Autowired
	private ColorRepository colorRepo;
	@Autowired
	private SizeRepository sizeRepo;
	@Autowired
	private ClothTypeRepository clothTypeRepo;

	@Value("${app.upload.image-dir}")
	private String uploadImageDir;

	/** STOCK IN **/
	@Transactional
	public String stockIn(StockInRequestDTO dto, MultipartFile imageFile) {

		// ✅ Save image using helper method
		if (imageFile != null && !imageFile.isEmpty()) {
			String imageUrl = saveImage(imageFile);
			dto.setImageUrl(imageUrl);
		}

		// 1. Fetch Category
		Category category = categoryRepo.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("❌ Category not found with ID: " + dto.getCategoryId()));

		// 2. Fetch Brand
		Brand brand = brandRepo.findById(dto.getBrandId())
				.orElseThrow(() -> new RuntimeException("❌ Brand not found with ID: " + dto.getBrandId()));

		// 3. Fetch Cloth Type
		ClothType clothType = clothTypeRepo.findById(dto.getClothTypeId())
				.orElseThrow(() -> new RuntimeException("❌ ClothType not found with ID: " + dto.getClothTypeId()));

		// 4. Create or fetch Product
		Product product = productRepo.findByDesignCode(dto.getDesignCode()).orElseGet(() -> {
			Product p = new Product();
			p.setDesignCode(dto.getDesignCode());
			p.setPattern(dto.getPattern());
			p.setBrand(brand);
			p.setClothType(clothType);
			p.setCategory(category);

			String generatedName = dto.getDesignCode() + " - " + brand.getBrand() + " - " + clothType.getClothType();
			p.setProductName(generatedName);

			if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
				p.setImageUrl(dto.getImageUrl());
			}

			return productRepo.save(p);
		});

		// ✅ Update image if product exists and new image is provided
		if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
			product.setImageUrl(dto.getImageUrl());
			productRepo.save(product);
		}

		// 5. Fetch Color
		Color color = colorRepo.findById(dto.getColorId())
				.orElseThrow(() -> new RuntimeException("❌ Color not found with ID: " + dto.getColorId()));

		// 6. Fetch Size
		Size size = sizeRepo.findById(dto.getSizeId())
				.orElseThrow(() -> new RuntimeException("❌ Size not found with ID: " + dto.getSizeId()));

		// 7. Create or update ProductVariant
		ProductVariant variant = variantRepo.findByProductSku(dto.getSku()).orElse(null);
		if (variant != null) {
			variant.setStockQty(variant.getStockQty() + dto.getQuantity());
			variant.setSellingPrice(dto.getSellingPrice());
		} else {
			variant = new ProductVariant();
			variant.setProductSku(dto.getSku());
			variant.setColor(color);
			variant.setSize(size);
			variant.setSellingPrice(dto.getSellingPrice());
			variant.setStockQty(dto.getQuantity());
			variant.setProduct(product);
		}
		variant = variantRepo.save(variant);

		// 8. Supplier
		Supplier supplier = supplierRepo.findBySupplierName(dto.getSupplierName()).orElseGet(() -> {
			Supplier s = new Supplier();
			s.setSupplierName(dto.getSupplierName());
			return supplierRepo.save(s);
		});

		// 9. Calculate Threshold Price
		BigDecimal thresholdPrice = dto.getBasePrice()
				.add(dto.getTaxPerUnit() != null ? dto.getTaxPerUnit() : BigDecimal.ZERO)
				.add(dto.getTransportPerUnit() != null ? dto.getTransportPerUnit() : BigDecimal.ZERO);

		// 10. Save Purchase
		Purchase purchase = new Purchase();
		purchase.setProductVariant(variant);
		purchase.setQuantity(dto.getQuantity());
		purchase.setThresholdPrice(thresholdPrice);
		purchase.setPurchaseDate(LocalDate.parse(dto.getPurchaseDate()));
		purchase.setSupplier(supplier);
		purchaseRepo.save(purchase);

		// 11. Stock Movement
		StockMovement movement = new StockMovement();
		movement.setProductVariant(variant);
		movement.setMovementType("IN");
		movement.setQuantity(dto.getQuantity());
		movement.setMovementDate(LocalDateTime.now());
		movement.setRemarks(dto.getRemarks());
		movementRepo.save(movement);

		return "✅ Stock added successfully!";
	}

	private String saveImage(MultipartFile imageFile) {
	    if (imageFile == null || imageFile.isEmpty()) {
	        return null;
	    }

	    try {
	        String originalFilename = imageFile.getOriginalFilename();
	        String extension = originalFilename != null && originalFilename.contains(".")
	                ? originalFilename.substring(originalFilename.lastIndexOf("."))
	                : "";

	        String uniqueFileName = UUID.randomUUID() + extension;

	        Path uploadPath = Paths.get(uploadImageDir);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        Path filePath = uploadPath.resolve(uniqueFileName);
	        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        // ✅ This is the public URL that will be used to access the image
	        return "/images/" + uniqueFileName;

	    } catch (IOException e) {
	        throw new RuntimeException("❌ Failed to store image file", e);
	    }
	}

	/** STOCK OUT **/
	public String stockOut(StockOutRequestDTO dto) {

	    // 1. Find product variant
	    Optional<ProductVariant> variantOpt = variantRepo.findByProductSku(dto.getSku());

	    if (variantOpt.isEmpty()) {
	        return "❌ SKU not found. Please enter a correct SKU.";
	    }

	    ProductVariant variant = variantOpt.get();

	    // 2. Check stock availability
	    if (variant.getStockQty() < dto.getQuantity()) {
	        return "❌ Insufficient stock for SKU: " + dto.getSku();
	    }

	    variant.setStockQty(variant.getStockQty() - dto.getQuantity());
	    variantRepo.save(variant);

	    // 3. Get latest purchase batch (for threshold price)
	    Optional<Purchase> lastPurchaseOpt = purchaseRepo.findTopByProductVariantOrderByPurchaseDateDesc(variant);
	    if (lastPurchaseOpt.isEmpty()) {
	        return "❌ No purchase record found for SKU: " + dto.getSku();
	    }
	    Purchase lastPurchase = lastPurchaseOpt.get();

	    // 4. Create SaleInvoice (minimal fields only)
	    SaleInvoice invoice = new SaleInvoice();
	    invoice.setInvoiceId("INV-" + System.currentTimeMillis()); // simple unique ID
	    invoice.setSaleDate(dto.getSaleDate().toLocalDate());
	    invoice.setTotalAmount(dto.getFinalPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
	    invoice = saleInvoiceRepo.save(invoice);

	    // 5. Save SaleItem
	    SaleItem saleItem = new SaleItem();
	    saleItem.setSaleInvoice(invoice);
	    saleItem.setProductVariant(variant);
	    saleItem.setQuantity(dto.getQuantity());
	    saleItem.setSellingPrice(dto.getSellingPrice());
	    saleItem.setFinalPrice(dto.getFinalPrice());
	    saleItem.setThresholdPriceAtSale(lastPurchase.getThresholdPrice());
	    saleItemRepo.save(saleItem);

	    // 6. Stock Movement
	    StockMovement movement = new StockMovement();
	    movement.setProductVariant(variant);
	    movement.setQuantity(dto.getQuantity());
	    movement.setMovementType("OUT");
	    movement.setMovementDate(dto.getSaleDate());
	    movement.setRemarks(dto.getRemarks());
	    movementRepo.save(movement);

	    return "✅ Stock OUT successful for SKU: " + dto.getSku() + ", Invoice: " + invoice.getInvoiceId();
	}
	public List<RecentStockInDTO> getRecentStockIns() {
	    // ✅ Fetch the 10 most recent purchases
	    List<Purchase> recentPurchases = purchaseRepo.findTop10ByOrderByPurchaseDateDesc();

	    return recentPurchases.stream().map(p -> {
	        ProductVariant variant = p.getProductVariant();
	        Product product = (variant != null) ? variant.getProduct() : null;

	        String productName = (product != null && product.getProductName() != null)
	                ? product.getProductName()
	                : "Unknown Product";

	        String sku = (variant != null && variant.getProductSku() != null)
	                ? variant.getProductSku()
	                : "N/A";

	        int quantityAdded = (p.getQuantity() != null) ? p.getQuantity() : 0;

	        String supplierName = (p.getSupplier() != null && p.getSupplier().getSupplierName() != null)
	                ? p.getSupplier().getSupplierName()
	                : "Unknown Supplier";

	        String imageUrl = (product != null && product.getImageUrl() != null)
	                ? product.getImageUrl()
	                : null;

	        return new RecentStockInDTO(
	                productName,
	                sku,
	                quantityAdded,
	                supplierName,
	                p.getPurchaseDate(),
	                imageUrl
	        );
	    }).collect(Collectors.toList());
	}

}
