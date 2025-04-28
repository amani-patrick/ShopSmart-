package com.amnii.ShopSmart.Repository;

import com.amnii.ShopSmart.Models.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByProductId(Long productId);

    @Query("SELECT s FROM Sale s JOIN FETCH s.product")
    List<Sale> findAllSales();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    double sumTotalAmountBetweenDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE DATE(s.saleDate) = :date")
    double sumTotalAmountByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    int sumQuantitySoldBetweenDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s WHERE DATE(s.saleDate) = :date")
    int sumQuantitySoldByDate(@Param("date") LocalDate date);

    List<Sale> findAllBySaleDateBetween(LocalDate startDate, LocalDate endDate);
} 