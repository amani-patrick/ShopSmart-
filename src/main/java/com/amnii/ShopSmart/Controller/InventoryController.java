package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Services.FileService;
import com.amnii.ShopSmart.Services.ProductService;
import com.amnii.ShopSmart.DTO.ProductDTO;
import com.amnii.ShopSmart.DTO.ErrorResponse;
import com.amnii.ShopSmart.Exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

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
            try {
                String filename = fileService.saveFile(image);
                String imageUrl = "/uploads/" + filename;
                product.setImageUrl(imageUrl);
            } catch (IOException e){}

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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("quantity") int quantity,
            @RequestParam("unit") String unit,
            @RequestParam("costPrice") double costPrice,
            @RequestParam("sellingPrice") double sellingPrice,
            @RequestParam("supplier") String supplier,
            @RequestParam("stockAlertLevel") int stockAlertLevel,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(name);
            productDTO.setCategory(category);
            productDTO.setStockQuantity(quantity);
            productDTO.setUnit(unit);
            productDTO.setCostPrice(costPrice);
            productDTO.setSellingPrice(sellingPrice);
            productDTO.setSupplier(supplier);
            productDTO.setStockAlertLevel(stockAlertLevel);
            productDTO.setImage(image);

            Product updated = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Product not found", e.getMessage()));
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("File storage error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error updating product", e.getMessage()));
        }
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
