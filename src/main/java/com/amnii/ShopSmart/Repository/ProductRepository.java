package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Product;
import com.amnii.ShopSmart.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findBySupplier(String supplier);
    List<Product> findByCategoryAndSupplier(String category, String supplier);
    List<Product> findByUser(User user);
    List<Product> findByUserAndCategory(User user, String category);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllDistinctCategories();
}
