package com.amnii.ShopSmart.Repository;


import com.amnii.ShopSmart.Models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
