package com.zalo.Spring_Zalo.ServiceImpl;

import com.zalo.Spring_Zalo.Controller.RegistrationController;
import com.zalo.Spring_Zalo.DTO.CustomerRewardDTO;
import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Exception.ApiNotFoundException;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.*;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CustomerRewardService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerRewardServiceImpl implements CustomerRewardService {
    @Autowired
    private ProductEventRepo productEventRepo;

    @Autowired
    private CustomerRewardRepo customerRewardRepo;

    @Autowired
    private RewardRepo rewardRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CustomerRepo customerRepo; 

    @Autowired
    private CustomerPointRepo customerPointRepo;

    private static final Logger logger = LoggerFactory.getLogger(CustomerRewardServiceImpl.class);
    @Override
    @Transactional(rollbackOn = { Throwable.class})
    public ApiResponse exchangeRewards(Integer customerId, Integer rewardId) throws Exception {
        Reward reward = rewardRepo.findById(rewardId).orElseThrow(() -> new ResourceNotFoundException("reward","rewardId",rewardId));
        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("customer","customerId",customerId));

        List<CustomerReward> customerReward = customerRewardRepo.findByCustomerIdAndReWardId(customerId, rewardId);
        Integer eventId = reward.getEvent().getId();
        CustomerPoint customerPoint = customerPointRepo.findByCustomerAndEvent(customerId,eventId).orElseThrow(()-> new ApiNotFoundException("you have no points !!!"));

        Event eventNow = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("event","eventId",rewardId));
        if(reward.getReward_type() ==  1){
            if (!customerReward.isEmpty() ){
                return  new ApiResponse("You're already have have this prize", false, 409);
            }
        }
        if(reward.getQuantity() <=0){
            throw new  ApiNotFoundException("Sorry, the product is out of stock !!!");
        }
        if(customerPoint.getPoint() < reward.getPointReward()){
            throw new  ApiNotFoundException("You do not have enough points to redeem this prize !!!");
        }

        CustomerReward newCustomerReward = new CustomerReward();
        newCustomerReward.setCustomer(customer);
        newCustomerReward.setEvent(eventNow);
        newCustomerReward.setReward(reward);
        if(reward.getReward_type() ==  1){
            newCustomerReward.setStatus(1);
        }else {
            newCustomerReward.setStatus(0);
        }
        //newCustomerReward.setStatus(eventNow.getType() == 1 ? 1 : 2);
        newCustomerReward.setExchangeRewardDate(LocalDateTime.now());

        // Update customer points
        customerPoint.setPoint(customerPoint.getPoint() - reward.getPointReward());

        // Save entities
        customerRewardRepo.saveAndFlush(newCustomerReward);
        customerPointRepo.saveAndFlush(customerPoint);
        reward.setQuantity(reward.getQuantity() - 1);
        rewardRepo.saveAndFlush(reward);
        if(newCustomerReward.getStatus() ==1 ){
            return  new ApiResponse("Congratulations on winning your prize !!!", true,200);
        }else{
            return  new ApiResponse("Congratulations on register your prize !!!", true,201);

    }
        
    }



    @Override
    public List<CustomerRewardDTO> getRewardId(Integer rewardId) throws Exception {
    List<CustomerReward> customerRewards = customerRewardRepo.findByReWardId(rewardId);
    return mapToCustomerRewardDTO(customerRewards);
}

    @Override
    public ByteArrayInputStream getExportDataExcel() throws IOException {
        List<CustomerReward> list = customerRewardRepo.findByStatus(0);
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(list);
        return byteArrayInputStream;
    }

    @Override
    public List<CustomerReward> convertExcelToList(InputStream is) {
        List<CustomerReward> list = new ArrayList<>();
        try{
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("data");
            int rowNumber =0;
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()){
                Row row = iterator.next();
                if(rowNumber == 0){
                    rowNumber ++;
                    continue;
                }
                Iterator<Cell> cells= row.iterator();
                int cid = 0;
                CustomerReward customerReward = new CustomerReward();
                Customer customer = new Customer();
                Reward reward = new Reward();
                while (cells.hasNext()){
                    Cell cell = cells.next();
                    switch (cid){
                        case 0:
                            customer = customerRepo.findById((int)cell.getNumericCellValue()).orElseThrow(()-> new ResourceNotFoundException("customer","customerId",(int)cell.getNumericCellValue()));
                            customerReward.setCustomer(customer);
                            break;
                        case 3:
                            customerReward.setStatus((int)cell.getNumericCellValue());
                            break;
                        default:
                            break;

                    }
                    cid++;
                }
                list.add(customerReward);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;

    }

    @Override
    public void addFileResultToPrize(List<CustomerReward> list) {
        for(CustomerReward customerReward:list){
            CustomerReward c = customerRewardRepo.getCustomerByStatus(customerReward.getId());
            if(c == null){
                break;
            }
            customerRewardRepo.saveAndFlush(customerReward);

        }
    }

    private List<CustomerRewardDTO> mapToCustomerRewardDTO(List<CustomerReward> customerRewards) {
        return customerRewards.stream()
                .map(this::mapToCustomerRewardDTO)
                .collect(Collectors.toList());
    }

    private CustomerRewardDTO mapToCustomerRewardDTO(CustomerReward customerReward) {
        CustomerRewardDTO customerRewardDTO = new CustomerRewardDTO();
        // Map các trường từ CustomerReward sang CustomerRewardDTO
        customerRewardDTO.setId(customerReward.getId());
        // Map các trường khác...

        // Lấy thông tin về Customer
        Customer customer = customerReward.getCustomer();
        if (customer != null) {
            customerRewardDTO.setCustomerId(customer.getId());
            customerRewardDTO.setCustomerName(customer.getName());
            // Các thông tin khác của Customer nếu cần thiết
        }

        // Lấy thông tin về Reward
        Reward reward = customerReward.getReward();
        if (reward != null) {
            customerRewardDTO.setRewardId(reward.getId());
            customerRewardDTO.setRewardName(reward.getName());
            // Các thông tin khác của Reward nếu cần thiết
        }

        // Lấy thông tin về Event
        Event event = customerReward.getEvent();
        if (event != null) {
            customerRewardDTO.setEventId(event.getId());
            customerRewardDTO.setEventName(event.getName());
            // Các thông tin khác của Event nếu cần thiết
        }

        return customerRewardDTO;
    }

}