package com.zalo.Spring_Zalo.Controller;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.zalo.Spring_Zalo.DTO.CustomerRewardDTO;
import com.zalo.Spring_Zalo.DTO.ProductDto;
import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Exception.ApiNotFoundException;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.zalo.Spring_Zalo.Repo.ProductEventRepo;
import com.zalo.Spring_Zalo.Repo.ProductRepo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {
     @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductEventRepo productEventRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CompanyRepo companyRepo;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @GetMapping("/")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "5") int pageSize,
                                                        @RequestParam(defaultValue = "1") int pageNumber){
    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
    Page<Product> productsPage = productRepo.findAllOrDerBOrderByCreateAt(pageable);
    Page<ProductDto> map = productsPage.map(this::mapToProductDto);
    return new ResponseEntity<>(map, HttpStatus.OK);
}
    @GetMapping("/company/{companyId}/event/{eventId}")
    public ResponseEntity<?> getAllProductsNotInCompanyAndEvent(@PathVariable("companyId") Integer companyId, @PathVariable("eventId") Integer eventId) {
        List<Product> productsPage = productRepo.findAllByNotInProductEvents(companyId, eventId);
        List<ProductDto> map = productsPage.stream().map(this::mapToProductDto).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("content", map);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search/company/{companyId}/event/{eventId}")
    public ResponseEntity<?> searchProductsNotInCompanyAndEvent(@RequestParam("key") String key,@PathVariable("companyId") Integer companyId, @PathVariable("eventId") Integer eventId) {
        List<Product> productsPage = productRepo.searchByNotInProductEvents(key,companyId, eventId);
        List<ProductDto> map = productsPage.stream().map(this::mapToProductDto).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("content", map);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAllProductsByCompany(@RequestParam(defaultValue = "5") int pageSize,
                                                                @RequestParam(defaultValue = "1") int pageNumber,
                                                                 @PathVariable Integer companyId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Product> productsPage = productRepo.findAllbyCompanyId(companyId,pageable);
        Page<ProductDto> map = productsPage.map(this::mapToProductDto);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestPart("name") String name,
                                                 @RequestPart("status") String status,
                                                 @RequestPart("point") String point,
                                                 @RequestPart(value = "file", required = false) MultipartFile file) {
        Product product =  productRepo.findById(id).orElseThrow(() -> new ApiNotFoundException("Not found product withh id: "+id));
        product.setName(name);
        product.setPoint(Integer.parseInt(point));
        product.setStatus(Boolean.parseBoolean(status));
        if(file != null){
            cloudinaryService.deleteImageUpload(product.getPublicId());

            Map data = cloudinaryService.upload(file);
            product.setPicture(String.valueOf(data.get("secure_url")));
            product.setPublicId(String.valueOf(data.get("public_id")));

        }
        Product savedProduct = productRepo.save(product); // Cập nhật thông tin của sản phẩm

        return ResponseEntity.ok(savedProduct); // Trả về sản phẩm đã được cập nhật
    }
    @PutMapping("/status/{id}")
    public ResponseEntity<Product> updateStatusProduct(@PathVariable Integer id) {
        Product product =  productRepo.findById(id).orElseThrow(() -> new ApiNotFoundException("Not found product withh id: "+id));
        product.setStatus(!product.isStatus());
        Product savedProduct = productRepo.save(product); // Cập nhật thông tin của sản phẩm
        return ResponseEntity.ok(savedProduct); // Trả về sản phẩm đã được cập nhật
    }
    @PostMapping(value = "/")
    public ResponseEntity<Product> createProduct(@RequestPart("name") String name,
                                                 @RequestPart("status") String status,
                                                 @RequestPart("point") String point,
                                                 @RequestPart("companyId") String companyId,
                                                 @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        Product product = new Product();
        Company company = companyRepo.findById(Integer.parseInt(companyId)).orElseThrow(() -> new ApiNotFoundException("Not found company withh id: "+companyId));

//        String fileName = saveFile(file);
        if(file != null) {
            Map data = cloudinaryService.upload(file);
            product.setPicture(String.valueOf(data.get("secure_url")));
            product.setPublicId(String.valueOf(data.get("public_id")));
        }
        product.setName(name);
        product.setPoint(Integer.parseInt(point));
        product.setStatus(Boolean.parseBoolean(status));
        product.setCompany(company);
        product.setCreateAt(LocalDateTime.now());

        Product pro = productRepo.saveAndFlush(product);
        return new ResponseEntity<>(pro,HttpStatus.CREATED);
    }
    @GetMapping("/{id}/events")
    public ResponseEntity<Event> getProductEvent(@PathVariable Long id) {
    // Tìm kiếm sản phẩm trong cơ sở dữ liệu dựa trên ID
    Optional<ProductEvent> productOptional = productEventRepo.findByProductId(id.intValue());
    if (productOptional.isPresent()) {
        Event eventInfo = productOptional.get().getEvent();
        if (eventInfo != null) {
            return ResponseEntity.ok(eventInfo); // Trả về thông tin event nếu tồn tại
        } else {
            return ResponseEntity.notFound().build(); // Trả về mã lỗi 404 nếu không tìm thấy event
        }
    } else {
        return ResponseEntity.notFound().build(); // Trả về mã lỗi 404 nếu không tìm thấy sản phẩm
    }
}
    public String saveFile(MultipartFile file) throws IOException {
        File directory = new File("images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        String uploadDir = System.getProperty("user.dir") + "/images/";
        File destination = new File(uploadDir + fileName);

        file.transferTo(destination);

        return fileName;
    }
    @DeleteMapping("/delete/{productId}")
    public  ResponseEntity<?> deleteProduct(@PathVariable Integer productId){
        Product product = productRepo.findById(productId).orElseThrow(() -> new ApiNotFoundException("Product not found with id: "+productId));
        productRepo.delete(product);
        cloudinaryService.deleteImageUpload(product.getPublicId());
        ApiResponse apiResponse = new ApiResponse("Delete product success",true,200);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    private ProductDto mapToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setStatus(product.isStatus());
        productDto.setPoint(product.getPoint());
        productDto.setPicture(product.getPicture());
        productDto.setPublicId(product.getPublicId());
        productDto.setCreateAt(product.getCreateAt());
        productDto.setCompany(product.getCompany().getName());
        return productDto;
    }
    
}
