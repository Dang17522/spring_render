package com.zalo.Spring_Zalo.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CustomerRewardDTO {
     private int id;
    private int status;
    private LocalDateTime exchangeRewardDate;
    private int customerId;
    private String customerName; // Add more customer fields if needed
    private int rewardId;
    private String rewardName; // Add more reward fields if needed
    private int eventId;
    private String eventName;
    private String image;
    private String phone;
    private int point;
    private String company;

    public CustomerRewardDTO() {
        this.id = id;
        this.status = status;
        this.exchangeRewardDate = exchangeRewardDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.rewardId = rewardId;
        this.rewardName = rewardName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.image = image;
        this.point = point;
    }
    
}
