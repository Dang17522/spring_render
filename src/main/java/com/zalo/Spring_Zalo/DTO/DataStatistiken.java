package com.zalo.Spring_Zalo.DTO;

public class DataStatistiken {
    private long eventCounts;
    private long totalPoints;
    private long totalCustomers;
    private double yourBalance;

    // Các phương thức getter và setter cho các trường dữ liệu
  


 

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }




    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public double getYourBalance() {
        return yourBalance;
    }

    public void setYourBalance(double yourBalance) {
        this.yourBalance = yourBalance;
    }

    public long getEventCounts() {
        return eventCounts;
    }

    public void setEventCounts(long eventCounts) {
        this.eventCounts = eventCounts;
    }


   

    
}
