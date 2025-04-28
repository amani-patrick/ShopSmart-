package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("quantity") int quantity,
            @RequestParam("unit") String unit,
            @RequestParam("costPrice") double costPrice,
            @RequestParam("sellingPrice") double sellingPrice,
            @RequestParam("supplier") String supplier, 
            @RequestParam("stockAlertLevel") int stockAlertLevel,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setStockQuantity(quantity);
        product.setUnit(unit);
        product.setCostPrice(costPrice);
        product.setSellingPrice(sellingPrice);
        product.setSupplier(supplier);
        product.setStockAlertLevel(stockAlertLevel);

        if (image != null && !image.isEmpty()) {
            String imageUrl = "/uploads/" + image.getOriginalFilename(); // Placeholder path
            product.setImageUrl(imageUrl);
        }

        Product saved = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // ✅ Update product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok("Product deleted successfully");
    }
    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    // ✅ Filter by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    // ✅ Filter by supplier
    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<Product>> getBySupplier(@PathVariable String supplier) {
        return ResponseEntity.ok(productService.getBySupplier(supplier));
    }

    // ✅ Combined filter
    @GetMapping("/search")
    public ResponseEntity<List<Product>> getByCategoryAndSupplier(
            @RequestParam String category,
            @RequestParam String supplier
    ) {
        return ResponseEntity.ok(productService.getByCategoryAndSupplier(category, supplier));
    }
}
