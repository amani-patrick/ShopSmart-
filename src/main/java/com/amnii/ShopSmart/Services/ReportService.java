package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.Sale;
import com.amnii.ShopSmart.Repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private SalesRepository salesRepository;

    public double getTotalSales(LocalDate startDate, LocalDate endDate) {
        return salesRepository.sumTotalAmountBetweenDates(startDate, endDate);
    }

    public double getTotalProfit(LocalDate startDate, LocalDate endDate) {
        // Assuming profit is calculated as total sales minus total costs
        // You may need to adjust this based on your business logic
        return getTotalSales(startDate, endDate) - getTotalCosts(startDate, endDate);
    }

    public int getTotalItemsSold(LocalDate startDate, LocalDate endDate) {
        return salesRepository.sumQuantitySoldBetweenDates(startDate, endDate);
    }

    private double getTotalCosts(LocalDate startDate, LocalDate endDate) {
        // Implement logic to calculate total costs if needed
        return 0.0; // Placeholder
    }

    public List<Sale> getSalesOverview(LocalDate startDate, LocalDate endDate) {
        // Implement logic to fetch sales data for the overview
        return salesRepository.findAllBySaleDateBetween(startDate, endDate);
    }
} 