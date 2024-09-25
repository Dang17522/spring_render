package com.zalo.Spring_Zalo.Controller;

import com.jayway.jsonpath.JsonPath;
import com.zalo.Spring_Zalo.Entities.Customer;
import com.zalo.Spring_Zalo.Entities.CustomerPoint;
import com.zalo.Spring_Zalo.Entities.Middleware;
import com.zalo.Spring_Zalo.Exception.TokenNotFoundException;
import com.zalo.Spring_Zalo.Repo.CustomerEventRepo;
import com.zalo.Spring_Zalo.Repo.CustomerPointRepo;
import com.zalo.Spring_Zalo.Repo.CustomerRepo;
import com.zalo.Spring_Zalo.Service.CustomerService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.zalo.Spring_Zalo.request.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private CustomerPointRepo customerPointRepo;

    @Autowired
    private CustomerEventRepo customerEventRepo;
    @GetMapping("/admin/")
    public ResponseEntity<Page<Customer>> getAllCustomersWithPagination(@RequestParam(defaultValue = "5") int pageSize,
                                                                        @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Customer> customersPage = customerRepo.findAll(pageable);

        return ResponseEntity.ok(customersPage);
    }

    @GetMapping("/admin/company/{companyId}")
    public ResponseEntity<Page<Customer>> getAllCustomersWithCompany(@PathVariable("companyId") Integer companyId,@RequestParam(defaultValue = "5") int pageSize,
                                                                     @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Customer> customersPage = customerEventRepo.findAllCustomerByCompanyId(companyId,pageable);
        return ResponseEntity.ok(customersPage);
    }

    // @PostMapping("/phone")
    // public ResponseEntity<Middleware> postMethodName(@RequestBody Middleware data) {
    //     //TODO: process POST request
        
    //     return ;
    // }
   
    /***
     * Update customer infomation 
     * @param customer
     * @param customerId
     * @return
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer,@PathVariable Integer customerId){
        Customer updateCustomer = customerService.updateCustomer(customer,customerId);
        return new ResponseEntity<>(updateCustomer, HttpStatus.OK);
    }

    @PutMapping("/updateAddress/{customerId}")
    public ResponseEntity<Customer> updateAdressCustomer(@RequestBody Customer customer,@PathVariable Integer customerId){
        Customer updateCustomer = customerService.updateAddressCustomer(customer,customerId);
        return new ResponseEntity<>(updateCustomer, HttpStatus.OK);
    }



    @GetMapping("/event/{eventId}/customer/{customerId}")
    public ResponseEntity<Map<String, Object>>  getUserById(@PathVariable("customerId") Integer customerId,@PathVariable("eventId") Integer eventId){
        Customer customer = customerService.getUserById(customerId);
        int customerPointValue= getCustomerPointValue(customer.getId(),eventId);
        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("customerPoint", customerPointValue);

    return new ResponseEntity<>(response, HttpStatus.OK);
    }




    @GetMapping("event/{eventId}/zaloId/{zaloId}")
    public ResponseEntity<Object> getUserByZaloId(@PathVariable("zaloId") String zaloId,@PathVariable("eventId") Integer eventId) {
        Customer customer = customerService.getUserByZaloId(zaloId);
        if (customer == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        int customerPointValue= getCustomerPointValue(customer.getId(),eventId);
        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("customerPoint", customerPointValue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private int getCustomerPointValue(int customerId, Integer eventId) {
        Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId, eventId);
        return customerPointOptional.map(CustomerPoint::getPoint).orElse(0);
    }
   
    @PostMapping("event/{eventId}/login")
    public ResponseEntity<Object> loginUser(@RequestBody RequestData requestData,@PathVariable Integer eventId){
        Customer existingCustomer = customerService.findUserByZaloId(requestData.getCustomer().getIdZalo());
        if(requestData.getMiddleware().getPhoneToken()== null ||requestData.getMiddleware().getAccessToken() == null){
            System.out.println("thiếu token  hoặc accesstoken ");
            System.out.println(requestData.getMiddleware().getAccessToken());
            return new ResponseEntity<>("Please login 1 more time ", HttpStatus.CONTINUE);
        }
        if(existingCustomer != null){    
            int customerPointValue = getCustomerPointValue(existingCustomer.getId(),eventId);
            customerService.updateCustomer(existingCustomer, existingCustomer.getId());
            return prepareLoginResponse(existingCustomer, customerPointValue, HttpStatus.ACCEPTED);
        }

        try {
            Customer savedCustomer = createCustomer(requestData);
            int customerPointValue = getCustomerPointValue(savedCustomer.getId(),eventId);
            return prepareLoginResponse(savedCustomer, customerPointValue, HttpStatus.CREATED);
        } catch (TokenNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred while creating customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

/**
 * this methob combie 2 object customer ìnfo and customer point in that event 
 * @param customer
 * @param customerPointValue
 * @param httpStatus
 * @return
 */
    private ResponseEntity<Object> prepareLoginResponse(Customer customer, int customerPointValue, HttpStatus httpStatus) {
        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("customerPoint", customerPointValue);
        return new ResponseEntity<>(response, httpStatus);
    }
       /**
     * this methob hanlde create new user without point
     * @param requestData
     * @return
     */
    public Customer createCustomer(RequestData requestData) throws IOException, TokenNotFoundException{
        ResourceBundle appkey = ResourceBundle.getBundle("application");
        String accessToken = requestData.getMiddleware().getAccessToken();
        String apiUrl =appkey.getString("zaloURL");
        String code = requestData.getMiddleware().getPhoneToken();
        String appSecretKey = appkey.getString("Appkey");
        if(requestData.getMiddleware().getAccessToken() == null ){
            return customerService.createAccounCustomer(requestData.getCustomer());
        }
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("access_token", accessToken);
            connection.setRequestProperty("code", code);
            connection.setRequestProperty("secret_key", appSecretKey);
    
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
    
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
    
            System.out.println("Response Body: " + responseBody.toString());
    
            String number = JsonPath.read(responseBody.toString(), "$.data.number");
            requestData.getCustomer().setPhone(number);
            System.out.println("Number: " + number);
    
            connection.disconnect();
            
            //Customer saveCustomer = customerService.createAccounCustomer(requestData.getCustomer());
            return customerService.createAccounCustomer(requestData.getCustomer());
        } catch (IOException e) {
            e.printStackTrace();
            throw e; // or handle the exception appropriately
        } catch (TokenNotFoundException e) {
            e.printStackTrace();
            throw e; // or handle the exception appropriately
        }
       
    }

    @GetMapping("/admin/exportFileExcel")
    public ResponseEntity<?> dowloadFileExcelApi() throws IOException {
        String fileName = "customerReward"+ LocalDateTime.now()+".xlsx";
        ByteArrayInputStream actualData = customerService.getExportDataExcel();
        InputStreamResource file = new InputStreamResource(actualData);
        ResponseEntity<?> body = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        return body;
    }

    @GetMapping("/admin/exportFileExcel/companyId/{companyId}")
    public ResponseEntity<?> dowloadFileExcelApiByCompanyId(@PathVariable("companyId") Integer companyId) throws IOException {
        String fileName = "customerEvent"+ LocalDateTime.now()+".xlsx";
        ByteArrayInputStream actualData = customerService.getExportDataExcelByCompanyId(companyId);
        InputStreamResource file = new InputStreamResource(actualData);
        ResponseEntity<?> body = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        return body;
    }
}
