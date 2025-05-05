package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.DTO.ProductDTO;
import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Repository.ProductRepository;
import com.amnii.ShopSmart.Repository.SalesRepository;
import com.amnii.ShopSmart.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.amnii.ShopSmart.Exception.FileStorageException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private SalesRepository salesRepository;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        Product existingProduct = optional.get();
        
        // Update only non-null fields
        if (productDTO.getName() != null) existingProduct.setName(productDTO.getName());
        if (productDTO.getCategory() != null) existingProduct.setCategory(productDTO.getCategory());
        if (productDTO.getStockQuantity() != null) existingProduct.setStockQuantity(productDTO.getStockQuantity());
        if (productDTO.getUnit() != null) existingProduct.setUnit(productDTO.getUnit());
        if (productDTO.getCostPrice() != null) existingProduct.setCostPrice(productDTO.getCostPrice());
        if (productDTO.getSellingPrice() != null) existingProduct.setSellingPrice(productDTO.getSellingPrice());
        if (productDTO.getSupplier() != null) existingProduct.setSupplier(productDTO.getSupplier());
        if (productDTO.getStockAlertLevel() != null) existingProduct.setStockAlertLevel(productDTO.getStockAlertLevel());

        // Handle image update if provided
        try {
            if (productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {
                // Delete old image if exists
                if (existingProduct.getImageUrl() != null) {
                    String oldFilename = existingProduct.getImageUrl().substring(existingProduct.getImageUrl().lastIndexOf('/') + 1);
                    fileService.deleteFile(oldFilename);
                }
                
                // Save new image
                String filename = fileService.saveFile(productDTO.getImage());
                existingProduct.setImageUrl("/uploads/" + filename);
            }
        } catch (IOException e) {
            throw new FileStorageException("Could not store file", e);
        }

        return productRepository.save(existingProduct);
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return false;
        }

        // Manually delete related sales
        Product product = optionalProduct.get();
        salesRepository.deleteAll(product.getSales());

        // Delete the product
        productRepository.delete(product);
        return true;
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getBySupplier(String supplier) {
        return productRepository.findBySupplier(supplier);
    }

    public List<Product> getByCategoryAndSupplier(String category, String supplier) {
        return productRepository.findByCategoryAndSupplier(category, supplier);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllDistinctCategories();
    }
}
