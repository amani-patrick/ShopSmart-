package com.amnii.ShopSmart.DTO;


public class SalesSummaryDTO {
    private double totalSalesToday;
    private double totalSalesThisWeek;
    private double totalSalesThisMonth;
    private int totalItemsSoldToday;

    // Getters and Setters
    public double getTotalSalesToday() {
        return totalSalesToday;
    }

    public void setTotalSalesToday(double totalSalesToday) {
        this.totalSalesToday = totalSalesToday;
    }

    public double getTotalSalesThisWeek() {
        return totalSalesThisWeek;
    }

    public void setTotalSalesThisWeek(double totalSalesThisWeek) {
        this.totalSalesThisWeek = totalSalesThisWeek;
    }

    public double getTotalSalesThisMonth() {
        return totalSalesThisMonth;
    }

    public void setTotalSalesThisMonth(double totalSalesThisMonth) {
        this.totalSalesThisMonth = totalSalesThisMonth;
    }

    public int getTotalItemsSoldToday() {
        return totalItemsSoldToday;
    }

    public void setTotalItemsSoldToday(int totalItemsSoldToday) {
        this.totalItemsSoldToday = totalItemsSoldToday;
    }
} 