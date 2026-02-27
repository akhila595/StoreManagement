package com.shopmanagement.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.RecentStockInDTO;
import com.shopmanagement.dto.RecentStockOutDTO;
import com.shopmanagement.dto.StockInRequestDTO;
import com.shopmanagement.dto.StockOutRequestDTO;
import com.shopmanagement.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockInController {

    private final StockService stockService;

    public StockInController(StockService stockService) {
        this.stockService = stockService;
    }

    /* =========================================================
       STOCK IN
       ========================================================= */

    @PostMapping(value = "/in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addStock(
            @RequestPart("data") StockInRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(
                stockService.stockIn(dto, image)
        );
    }

    /* =========================================================
       STOCK OUT
       ========================================================= */

    @PostMapping("/out")
    public ResponseEntity<String> stockOut(
            @RequestBody StockOutRequestDTO dto) {

        return ResponseEntity.ok(
                stockService.stockOut(dto)
        );
    }

    /* =========================================================
       RECENT STOCK IN
       ========================================================= */

    @GetMapping("/recent-ins")
    public ResponseEntity<List<RecentStockInDTO>> getRecentStockIns() {

        return ResponseEntity.ok(
                stockService.getRecentStockIns()
        );
    }

    /* =========================================================
       RECENT STOCK OUT
       ========================================================= */

    @GetMapping("/recent-outs")
    public ResponseEntity<List<RecentStockOutDTO>> getRecentStockOuts() {

        return ResponseEntity.ok(
                stockService.getRecentStockOuts()
        );
    }
}