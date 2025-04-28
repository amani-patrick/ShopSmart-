package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.DTO.SaleDTO;
import com.amnii.ShopSmart.DTO.SalesSummaryDTO;
import com.amnii.ShopSmart.DTO.SaleResponseDTO;
import com.amnii.ShopSmart.Models.*;
import com.amnii.ShopSmart.Services.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3030")
@RequestMapping("/sales")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @Autowired
    private SalesService salesService;

    // Create a new sale
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody SaleDTO saleDTO) {
        logger.info("Received sale data: {}", saleDTO);
        if (saleDTO.getProductId() == null) {
            logger.error("Product ID is null in the request");
            return ResponseEntity.badRequest().build();
        }
        Sale createdSale = salesService.createSale(saleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    // Get all sales
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        return ResponseEntity.ok(salesService.getAllSales());
    }

    // Get sale by ID
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return salesService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Get sales by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Sale>> getSalesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(salesService.getSalesByProduct(productId));
    }

    // Update sale
    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable Long id, @RequestBody SaleDTO saleDTO) {
        Sale updated = salesService.updateSale(id, saleDTO);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updated);
    }

    // Delete sale
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSale(@PathVariable Long id) {
        boolean deleted = salesService.deleteSale(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sale not found");
        }
        return ResponseEntity.ok("Sale deleted successfully");
    }

    // Get sales summary
    @GetMapping("/summary")
    public ResponseEntity<SalesSummaryDTO> getSalesSummary() {
        return ResponseEntity.ok(salesService.getSalesSummary());
    }
} 