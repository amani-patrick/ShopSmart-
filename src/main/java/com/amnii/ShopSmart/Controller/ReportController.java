package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestParam String period,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalSales", reportService.getTotalSales(startDate, endDate));
        reportData.put("totalProfit", reportService.getTotalProfit(startDate, endDate));
        reportData.put("totalItemsSold", reportService.getTotalItemsSold(startDate, endDate));
        reportData.put("salesOverview", reportService.getSalesOverview(startDate, endDate));

        return ResponseEntity.ok(reportData);
    }
} 