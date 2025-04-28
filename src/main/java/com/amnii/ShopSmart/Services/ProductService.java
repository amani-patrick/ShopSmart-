package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Repository.ProductRepository;
import com.amnii.ShopSmart.Repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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

    public Product updateProduct(Long id, Product product) {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isEmpty()) return null;

        Product existingProduct = optional.get();
        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setCostPrice(product.getCostPrice());
        existingProduct.setSellingPrice(product.getSellingPrice());
        existingProduct.setSupplier(product.getSupplier());
        existingProduct.setStockAlertLevel(product.getStockAlertLevel());
        existingProduct.setImageUrl(product.getImageUrl());

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
