package com.zalo.Spring_Zalo.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemReceipt {
    private String itemName;
    private int quantity;

    private double price;

}
