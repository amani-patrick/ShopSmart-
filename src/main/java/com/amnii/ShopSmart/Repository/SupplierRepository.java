package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Supplier;
import com.amnii.ShopSmart.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByUser(User user);
    List<Supplier> findByUserAndIsActive(User user, boolean isActive);
}
