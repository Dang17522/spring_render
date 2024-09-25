package com.zalo.Spring_Zalo.request;

import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Entities.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataProductEventRequest {
    private int eventId;
    private String productId;
}
