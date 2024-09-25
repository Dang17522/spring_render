package com.zalo.Spring_Zalo.Service;

import org.springframework.http.ResponseEntity;

import com.zalo.Spring_Zalo.Entities.ProductEvent;

public interface ProductEventService {
 ResponseEntity<ProductEvent> createProductEvent( ProductEvent eventDetails, Integer companyId);
}
