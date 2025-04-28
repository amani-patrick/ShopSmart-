package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByIsPaid(boolean isPaid);
} 