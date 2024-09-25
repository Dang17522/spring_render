package com.zalo.Spring_Zalo.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductEventDto {

    private int id;
    private LocalDateTime createAt;
    private String company;
    private int point;
    private String productName;
    private String picture;
    private String eventName;
    private int eventId;
    private int productId;
}
