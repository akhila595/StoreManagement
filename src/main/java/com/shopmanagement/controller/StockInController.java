package com.shopmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopmanagement.dto.LowStockProductDTO;
import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.dto.StockInRequestDTO;
import com.shopmanagement.dto.StockOutRequestDTO;
import com.shopmanagement.service.ReportService;
import com.shopmanagement.service.StockService;


@RestController
@RequestMapping("/api")
public class StockInController {
	
	@Autowired
	private StockService stockMovementService;
	
	@Autowired
	private ReportService reportService;

	@PostMapping("/stock-in")
	public ResponseEntity<String> addStock(@RequestBody StockInRequestDTO dto) {
		String result = stockMovementService.stockIn(dto);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/stock-out")
	public ResponseEntity<String> stockOut(@RequestBody StockOutRequestDTO dto) {
		String result = stockMovementService.stockOut(dto);
		return ResponseEntity.ok(result);
	}
	
	 @GetMapping("/products")
	    public List<ProductDTO> getAllProducts() {
	        return reportService.getAllProducts();
	    }
	

}
