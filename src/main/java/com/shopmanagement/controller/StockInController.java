package com.shopmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity<String> addStock(
            @RequestBody StockInRequestDTO dto) {

        return ResponseEntity.ok(
                stockService.stockIn(dto)
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