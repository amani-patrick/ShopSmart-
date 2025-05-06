package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Debt;
import com.amnii.ShopSmart.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUser(User user);
    List<Debt> findByUserAndIsPaid(User user, boolean isPaid);
} 