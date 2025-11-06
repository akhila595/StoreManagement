package com.shopmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.dto.RecentStockInDTO;
import com.shopmanagement.dto.RecentStockOutDTO;
import com.shopmanagement.dto.StockInRequestDTO;
import com.shopmanagement.dto.StockOutRequestDTO;
import com.shopmanagement.service.ReportService;
import com.shopmanagement.service.StockService;

@RestController
@RequestMapping("/api")
public class StockInController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/stock-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addStock(
        @RequestPart("data") StockInRequestDTO dto,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String result = stockService.stockIn(dto, image);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/stock-out")
    public ResponseEntity<String> stockOut(@RequestBody StockOutRequestDTO dto) {
        String result = stockService.stockOut(dto);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/recent-stock-ins")
    public ResponseEntity<List<RecentStockInDTO>> getRecentStockIns() {
        List<RecentStockInDTO> recentStockIns = stockService.getRecentStockIns();
        return ResponseEntity.ok(recentStockIns);
    }
    
    @GetMapping("/recent-stock-outs")
    public ResponseEntity<List<RecentStockOutDTO>> getRecentStockOuts() {
        List<RecentStockOutDTO> recentStockOuts = stockService.getRecentStockOuts();
        return ResponseEntity.ok(recentStockOuts);
    }
}
