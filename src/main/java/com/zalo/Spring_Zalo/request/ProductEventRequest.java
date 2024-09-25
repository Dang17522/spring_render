package com.zalo.Spring_Zalo.request;

import java.util.List;

import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Entities.Product;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProductEventRequest {
    private Event event;
    private List<Product> productList;
}
