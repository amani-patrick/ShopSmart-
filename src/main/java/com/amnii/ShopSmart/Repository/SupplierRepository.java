package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsActive(boolean isActive);
}
