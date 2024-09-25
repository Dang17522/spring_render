package com.zalo.Spring_Zalo.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Entities.Product;
import com.zalo.Spring_Zalo.Entities.ProductEvent;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Repo.ProductEventRepo;
import com.zalo.Spring_Zalo.Repo.ProductRepo;
import com.zalo.Spring_Zalo.Service.ProductEventService;
import com.zalo.Spring_Zalo.request.ProductEventRequest;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductEventServiceImpl implements ProductEventService {

    @Autowired
    private ProductEventRepo productEventRepository;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public ResponseEntity<ProductEvent> createProductEvent(ProductEvent eventDetails, Integer companyId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createProductEvent'");
    }

    // @Override
    // @Transactional
    // public ResponseEntity<ProductEventRequest> createProductEvent(Event event, List<Product> productList) {
    //     if (eventDetails.getProduct() == null || eventDetails.getEvent() == null) {
    //         System.out.println("Product or Event is empty");
    //         return ResponseEntity.badRequest().build();
    //     }

    //     Event event = eventDetails.getEvent();
    //     Product product = eventDetails.getProduct();

    //     // Save Event and Product
    //     eventRepo.save(event);
    //     productRepo.save(product);

    //     // Set the Event and Product in the ProductEvent
    //     eventDetails.setEvent(event);
    //     eventDetails.setProduct(product);

    //     // Save the ProductEvent (assuming ProductEvent has relationships with Event and Product)
    //     ProductEvent savedProductEvent = productEventRepository.save(eventDetails);

    //     if (savedProductEvent != null) {
    //         return ResponseEntity.ok(savedProductEvent);
    //     } else {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
}

