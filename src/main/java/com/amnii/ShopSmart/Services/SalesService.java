package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.DTO.SaleDTO;
import com.amnii.ShopSmart.DTO.SalesSummaryDTO;
import com.amnii.ShopSmart.DTO.SaleResponseDTO;
import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Models.Sale;
import com.amnii.ShopSmart.Repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private ProductService productService;

    public Sale createSale(SaleDTO saleDTO) {
        if (saleDTO.getProductId() == null || saleDTO.getQuantitySold() <= 0 || saleDTO.getTotalAmount() <= 0) {
            logger.error("Invalid sale data provided: {}", saleDTO);
            throw new IllegalArgumentException("Invalid sale data provided");
        }
        logger.info("Creating sale for product ID: {}", saleDTO.getProductId());
        // Fetch the product
        Product product = productService.getProductById(saleDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + saleDTO.getProductId()));

        // Check product stock
        if (product.getStockQuantity() < saleDTO.getQuantitySold()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // Create the sale
        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setQuantitySold(saleDTO.getQuantitySold());
        sale.setTotalAmount(saleDTO.getTotalAmount());
        sale.setSaleDate(LocalDate.now());

        // Update product stock
        product.setStockQuantity(product.getStockQuantity() - saleDTO.getQuantitySold());
        productService.updateProduct(product.getId(), product);

        // Save and return the sale
        return salesRepository.save(sale);
    }

    public List<SaleResponseDTO> getAllSales() {
        List<Sale> sales = salesRepository.findAllSales();
        return sales.stream()
                .map(this::convertToSaleResponseDTO)
                .collect(Collectors.toList());
    }

    private SaleResponseDTO convertToSaleResponseDTO(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setProductId(sale.getProduct().getId());
        dto.setQuantitySold(sale.getQuantitySold());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setSaleDate(sale.getSaleDate());
        return dto;
    }

    public Optional<Sale> getSaleById(Long id) {
        return salesRepository.findById(id);
    }

    public List<Sale> getSalesByProduct(Long productId) {
        return salesRepository.findByProductId(productId);
    }

    public Sale updateSale(Long id, SaleDTO saleDTO) {
        // Validate input
        if (saleDTO == null || saleDTO.getProductId() == null || saleDTO.getQuantitySold() <= 0) {
            throw new IllegalArgumentException("Invalid sale data provided");
        }

        // Find existing sale
        Sale existingSale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));

        // Fetch the product
        Product product = productService.getProductById(saleDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + saleDTO.getProductId()));

        // Calculate the stock difference
        int quantityDifference = saleDTO.getQuantitySold() - existingSale.getQuantitySold();

        // Check if there's enough stock for the update
        if (product.getStockQuantity() < quantityDifference) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // Update the sale details
        existingSale.setProduct(product);
        existingSale.setQuantitySold(saleDTO.getQuantitySold());
        existingSale.setTotalAmount(saleDTO.getTotalAmount());

        // Update product stock
        product.setStockQuantity(product.getStockQuantity() - quantityDifference);
        productService.updateProduct(product.getId(), product);

        // Save and return the updated sale
        return salesRepository.save(existingSale);
    }

    public boolean deleteSale(Long id) {
        if (!salesRepository.existsById(id)) {
            return false;
        }
        salesRepository.deleteById(id);
        return true;
    }

    @Cacheable("salesSummary")
    public SalesSummaryDTO getSalesSummary() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // Calculate summary statistics
        double totalSalesToday = salesRepository.sumTotalAmountByDate(today);
        double totalSalesThisWeek = salesRepository.sumTotalAmountBetweenDates(startOfWeek, today);
        double totalSalesThisMonth = salesRepository.sumTotalAmountBetweenDates(startOfMonth, today);
        int totalItemsSoldToday = salesRepository.sumQuantitySoldByDate(today);

        // Create and return the summary DTO
        SalesSummaryDTO summary = new SalesSummaryDTO();
        summary.setTotalSalesToday(totalSalesToday);
        summary.setTotalSalesThisWeek(totalSalesThisWeek);
        summary.setTotalSalesThisMonth(totalSalesThisMonth);
        summary.setTotalItemsSoldToday(totalItemsSoldToday);

        return summary;
    }
} 