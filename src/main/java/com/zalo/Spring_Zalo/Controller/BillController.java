package com.zalo.Spring_Zalo.Controller;

import java.util.Optional;

import com.zalo.Spring_Zalo.DTO.BillDto;
import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zalo.Spring_Zalo.Repo.BillRepo;
import com.zalo.Spring_Zalo.Repo.CustomerPointRepo;
import com.zalo.Spring_Zalo.Repo.CustomerRepo;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.request.BillRequest;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bill")
public class BillController {
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private CustomerPointRepo customerPointRepo;
    
     @GetMapping("/")
    public ResponseEntity<?> getAllBill(@RequestParam(defaultValue = "1000") int pageSize,
                                                    @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Bill> bill = billRepo.findAll(pageable);
    
        Page<BillDto> map = bill.map(this::mapToDto);
    
        return ResponseEntity.ok(map);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAllBillByCompanyId(@PathVariable("companyId") Integer companyId,@RequestParam(defaultValue = "1000") int pageSize,
                                        @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Bill> bill = billRepo.findByCompanyId(companyId,pageable);
        Page<BillDto> map = bill.map(this::mapToDto);
        return ResponseEntity.ok(map);
    }
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getAllBillByEventId(@PathVariable("eventId") Integer eventId,@RequestParam(defaultValue = "1000") int pageSize,
                                                   @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Bill> bill = billRepo.findByEventId(eventId,pageable);
        Page<BillDto> map = bill.map(this::mapToDto);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/company/{companyId}/event/{eventId}")
    public ResponseEntity<?> getAllBillByCompanyIdAndEventId(@PathVariable("companyId") Integer companyId,@PathVariable("eventId") Integer eventId,@RequestParam(defaultValue = "1000") int pageSize,
                                                   @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Bill> bill = billRepo.findByCompanyIdAndEventId(companyId,eventId,pageable);
        Page<BillDto> map = bill.map(this::mapToDto);
        return ResponseEntity.ok(map);
    }
         @PutMapping("/{id}/disable")
         @CrossOrigin(maxAge = 3600)
        public ResponseEntity<String> disableBill(@PathVariable("id") Long id) {
        // Gọi service để thực hiện vô hiệu hóa hóa đơn với id nhận được
        // billService.disableBill(id);
        Optional<Bill> billdata = billRepo.findById(id.intValue());
        if (billdata.isPresent()){
            Bill bill = billdata.get();
            bill.setStatus(EnumManager.Billtatus.DISABLE);
            billRepo.save(bill);
        } else {
            return new ResponseEntity<>("Bill not found", HttpStatus.NOT_FOUND); // Trả về thông báo lỗi nếu không tìm thấy hóa đơn với id nhận được
        }
    
        return new ResponseEntity<>("Bill disabled successfully", HttpStatus.OK); // Trả về thông báo thành công nếu cập nhật thành công
    }
    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable("id") int id, @RequestBody BillRequest updatedBill) {
        // Tìm hóa đơn trong cơ sở dữ liệu
        Bill existingBill = billRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("bill","billId",id));
        Customer customer = customerRepo.findById(updatedBill.getCustomerId()).orElseThrow(()-> new ResourceNotFoundException("customer","customerId",updatedBill.getCustomerId()));
        Event event = eventRepo.findById(updatedBill.getEventId()).orElseThrow(()-> new ResourceNotFoundException("event","eventId",updatedBill.getEventId()));

        Optional<CustomerPoint> customerPoint = customerPointRepo.findByCustomerAndEvent(existingBill.getCustomerId(), existingBill.getEvent().getId());
        if (customerPoint.isPresent()) {
            CustomerPoint pointToUpdate = customerPoint.get();
            pointToUpdate.setPoint(pointToUpdate.getPoint() + existingBill.getPoint());
            customerPointRepo.save(pointToUpdate);
        } else {
           CustomerPoint newPoint = new CustomerPoint();
            newPoint.setPoint(updatedBill.getPoint());
            newPoint.setCustomer(customer);
            newPoint.setEvent(event);
            customerPointRepo.save(newPoint);
        }
        

        // Cập nhật thông tin hóa đơn với dữ liệu mới từ yêu cầu PUT
        existingBill.setStatus(updatedBill.getStatus());
        existingBill.setPoint(updatedBill.getPoint());

        // Lưu hóa đơn đã cập nhật vào cơ sở dữ liệu
        Bill bill =  billRepo.save(existingBill);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    private BillDto mapToDto(Bill bill){
         Customer customer = customerRepo.findById(bill.getCustomerId()).get();
         return BillDto.builder()
                 .id(bill.getId())
                 .status(bill.getStatus())
                 .billCode(bill.getBillCode())
                 .scanDate(bill.getScanDate())
                 .createDate(bill.getCreateDate())
                 .updateDate(bill.getUpdateDate())
                 .point(bill.getPoint())
                 .image(bill.getImage())
                 .deleteFlag(bill.getDeleteFlag())
                 .eventId(bill.getEvent().getId())
                 .customerId(bill.getCustomerId())
                 .eventName(bill.getEventName())
                 .zaloId(customer.getIdZalo())
                 .phone(customer.getPhone())
                 .arrayItem(bill.getArrayItem())
                 .company(bill.getEvent().getCompany().getName())
                 .buyDate(bill.getBuyDate())
                 .infomation(bill.getInfomation())
                 .build();
    }
}