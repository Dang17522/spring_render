package com.zalo.Spring_Zalo.Controller;

import com.zalo.Spring_Zalo.DTO.ProductDto;
import com.zalo.Spring_Zalo.DTO.ProductEventDto;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Entities.Product;
import com.zalo.Spring_Zalo.Entities.ProductEvent;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Repo.ProductEventRepo;
import com.zalo.Spring_Zalo.Repo.ProductRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.ServiceImpl.ProductEventServiceImpl;
import com.zalo.Spring_Zalo.request.DataProductEventRequest;
import com.zalo.Spring_Zalo.request.ProductEventRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/productEvent")
public class ProductEventController {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private ProductEventRepo productEventRepo;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private ProductEventServiceImpl service;  
  @GetMapping("/event/{eventId}")
public ResponseEntity<?> getListProductEvent(@PathVariable("eventId") Integer eventId,
                                                         @RequestParam(defaultValue = "5") int pageSize,
                                                         @RequestParam(defaultValue = "1") int pageNumber){
      Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
      Page<ProductEvent> productEventsPage = productEventRepo.findByEventIdOrDerBOrderByCreateAt(eventId,pageable);
      Page<ProductEventDto> map = productEventsPage.map(this::mapToDto);
      return new ResponseEntity<>(map, HttpStatus.OK);
  }

    // @PostMapping("/company/{companyId}")
    // public ResponseEntity<ProductEvent> createEvent(@RequestBody ProductEvent eventDetails) {
    //     System.out.println(eventDetails);
    //     if (eventDetails.getEvent() == null) {
    //         System.out.println("Events info is empty");
    //         return ResponseEntity.badRequest().build();
    //     }
    //     if (eventDetails.getProduct() == null) {
    //         System.out.println("product info is empty");
    //         return ResponseEntity.badRequest().build();
    //     }
    //     Event event = new Event();
    //     Product product = new Product();
    //     event = eventDetails.getEvent();
    //     product = eventDetails.getProduct();

    //     // Save the Event and Product entities
    //     eventRepo.save(event);
    //     productRepo.save(product);

    //     // Set the Event and Product in the ProductEvent
    //     eventDetails.setEvent(event);
    //     eventDetails.setProduct(product);

    //     // Save the ProductEvent
    //     ProductEvent savedProductEvent = productEventRepo.save(eventDetails);

    //     if (savedProductEvent != null) {
    //         return ResponseEntity.ok(savedProductEvent);
    //     } else {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }


    @PostMapping("/")
    public ResponseEntity<?> createProductEvent(@RequestBody ProductEventRequest productEventRequest) {
        Event event = productEventRequest.getEvent();
        List<Product> productList = productEventRequest.getProductList();

        if (event == null) {
            ApiResponse response = new ApiResponse("Không có thông tin Sự kiện", false, 400);
            return ResponseEntity.badRequest().body(response);
        }

        Company company = event.getCompany();
        if (company == null) {
            ApiResponse response = new ApiResponse("Không có thông tin công ty", false, 400);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Company> existingCompany = companyRepo.findById(company.getId());
        if (existingCompany.isEmpty()) {
            companyRepo.saveAndFlush(company);
        }

        Optional<Event> existingEvent = eventRepo.findById(event.getId());
        if (existingEvent.isEmpty()) {
            eventRepo.saveAndFlush(event);
        }

        if (productList.isEmpty()) {
            ApiResponse response = new ApiResponse("Không có thông tin Sản phẩm", false, 400);
            return ResponseEntity.badRequest().body(response);
        }

        for (Product product : productList) {
            Optional<Product> existingProduct = productRepo.findById(product.getId());
            if (existingProduct.isEmpty()) {
                productRepo.saveAndFlush(product);
            }

            ProductEvent productEvent = new ProductEvent();
            productEvent.setEvent(event);
            productEvent.setProduct(product);
            productEventRepo.save(productEvent);
        }

        return ResponseEntity.ok("ProductEvent created successfully.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProductEvent(@PathVariable("id") Integer id, @RequestBody ProductEventDto productEventRequest) {
        Product product = productRepo.findById(productEventRequest.getId()).orElseThrow(()-> new ResourceNotFoundException("Product","id",id));
        product.setPoint(productEventRequest.getPoint());
        Product updateProduct  = productRepo.saveAndFlush(product);
        return ResponseEntity.ok(updateProduct);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DataProductEventRequest data) {
        Event event = eventRepo.findById(data.getEventId()).orElseThrow(()-> new ResourceNotFoundException("Event","id",data.getEventId()));
        String dataString = data.getProductId().replace("[", "").replace("]", "");
        String[] listDataProduct = dataString.split(",");
        for (String productId : listDataProduct) {
            int id = Integer.parseInt(productId);
            Product product = productRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product","id",id));
            ProductEvent productEvent = new ProductEvent();
            productEvent.setEvent(event);
            productEvent.setProduct(product);
            productEvent.setCreateAt(LocalDateTime.now());
            productEventRepo.saveAndFlush(productEvent);
        }
       ApiResponse response = new ApiResponse("Create ProductEvent successfully",true,200);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProductEvents(@RequestParam(defaultValue = "5") int pageSize,
                                            @RequestParam(defaultValue = "1") int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ProductEvent> productEventsPage = productEventRepo.findAllOrDerBOrderByCreateAt(pageable);
        Page<ProductEventDto> map = productEventsPage.map(this::mapToDto);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAllProductEventsByCompany(@PathVariable("companyId") Integer companyId,
                                                          @RequestParam(defaultValue = "5") int pageSize,
                                                          @RequestParam(defaultValue = "1") int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ProductEvent> productEventsPage = productEventRepo.findByCompanyOrDerBOrderByCreateAt(companyId,pageable);
        Page<ProductEventDto> map = productEventsPage.map(this::mapToDto);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/company/{companyId}/event/{eventId}")
    public ResponseEntity<?> getAllProductEventsByCompanyAndEvent(@PathVariable("companyId") Integer companyId,@PathVariable("eventId") Integer eventId,
                                                          @RequestParam(defaultValue = "5") int pageSize,
                                                          @RequestParam(defaultValue = "1") int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ProductEvent> productEventsPage = productEventRepo.findByCompanyAndEventOrDerBOrderByCreateAt(companyId,eventId,pageable);
        Page<ProductEventDto> map = productEventsPage.map(this::mapToDto);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProductEvent(@PathVariable Integer productId){
        ProductEvent productEvent = productEventRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("ProductEvent","id",productId));
        productEventRepo.delete(productEvent);
        ApiResponse apiResponse = new ApiResponse("Delete productEvent success",true,200);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    private ProductEventDto mapToDto(ProductEvent productEvent) {
       ProductEventDto dto = ProductEventDto.builder().id(productEvent.getId())
               .createAt(productEvent.getCreateAt())
               .eventName(productEvent.getEvent().getName())
               .eventName(productEvent.getEvent().getName())
               .productName(productEvent.getProduct().getName())
               .point(productEvent.getProduct().getPoint())
               .company(productEvent.getEvent().getCompany().getName())
               .picture(productEvent.getProduct().getPicture())
               .productId(productEvent.getProduct().getId())
               .eventId(productEvent.getEvent().getId())
               .build();
        return dto;
    }

}
