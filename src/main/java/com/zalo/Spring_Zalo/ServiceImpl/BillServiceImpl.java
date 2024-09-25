package com.zalo.Spring_Zalo.ServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.Spring_Zalo.Controller.CustomerEventController;
import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.*;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.zalo.Spring_Zalo.Service.BillService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class BillServiceImpl implements BillService {
    @Autowired
    private BillRepo billRepository;

    @Autowired
    private ProductEventRepo productEventRepo;

    @Autowired
    private CustomerPointRepo customerPointRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CloudinaryService cloudinaryService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerEventController.class);
    public Bill findBillByCode(String billCode) {
        if (billCode != null) {
            return billRepository.findByBillCode(billCode);
        } else {
            throw new IllegalArgumentException("Bill code cannot be null");
        }
    }

    public Bill savebill(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public Receipt jsonScanReceipt(String json, Locale currentLocale, Integer eventId) throws JsonProcessingException {
        boolean checkListItems = false;
        boolean checkReceipt = false;
        Integer totalPoint = 0;
        ObjectMapper objectMapper = new ObjectMapper();

        ParsedResult parsedResult = objectMapper.readValue(json, ParsedResult.class);
        Receipt receipt = new Receipt();
        Bill bill = new Bill();

        List<ItemReceipt> itemReceiptList = new ArrayList<>();
        if (parsedResult != null) {
            System.out.println("parsedResult: "+parsedResult.getErrorMessage());
            System.out.println("parsedResult: "+parsedResult.getErrorDetails());
            ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);
            List<Line> lineTextList = parsedResult.getParsedResults().get(0).getTextOverlay().getLines();

            String keyStartBillNo = keyWord.getString("keyStartBillNo");
            String keyStartBillNo2 = keyWord.getString("keyStartBillNo2");
            String keyStartListItem = keyWord.getString("keyStartListItem");
            String keyStartListItem2 = keyWord.getString("keyStartListItem2");
            String keyStartListItem3 = keyWord.getString("keyStartListItem3");
            String keyEndListItem = keyWord.getString("keyEndListItem");
            String keyEndListItem2 = keyWord.getString("keyEndListItem2");
            String keyCheckMonney = keyWord.getString("keyCheckMonney");
            String keyCheckMonney2 = keyWord.getString("keyCheckMonney2");
            String keyCheckMonney3 = keyWord.getString("keyCheckMonney3");
            String keyCheckVat = keyWord.getString("keyCheckVat");
            String keyCheckInfo = keyWord.getString("keyCheckInfo");
            String billingDate = keyWord.getString("keyBillingDate");
            String infomation ="";
            boolean checkInfo = false;
            if(lineTextList.isEmpty()){
                receipt.setStatus(EnumManager.Billtatus.ERROR);
                return receipt;
            }
            for (int i = 0; i < lineTextList.size(); i++) {
                Line line = lineTextList.get(i);
                System.out.println("line: " + line.getLineText());
                Line lineContinute = line;
                Line lineStep2 = line;
                Line lineStep3 = line;
                Line lineStep4 = line;
                if (i < lineTextList.size() - 1) {
                    lineContinute = lineTextList.get(i + 1);
                }
                if (i < lineTextList.size() - 2) {
                    lineStep2 = lineTextList.get(i + 2);
                }
                if (i < lineTextList.size() - 3) {
                    lineStep3 = lineTextList.get(i + 3);
                }
                if (i < lineTextList.size() - 4) {
                    lineStep4 = lineTextList.get(i + 4);
                }
                if(line.getLineText().contains(keyCheckInfo)){
                    checkInfo = true;
                    receipt.setInfomation(infomation);
                }
                if(!checkInfo){
                    infomation += line.getLineText() +" ";
                }
                logger.info("line: " + line.getLineText());
                if(line.getLineText().contains(billingDate)){
                    String buyData = line.getLineText().replace(billingDate, "").trim();
                    receipt.setBuyDate(buyData);
                }
                if (line.getLineText().contains(keyStartBillNo) || line.getLineText().contains(keyStartBillNo2)) {
                    if (line.getLineText().contains(keyStartBillNo)) {
                        bill = findBillByCode(lineContinute.getLineText());
                        receipt.setReceiptId(lineContinute.getLineText());
                    } else {
                        String keyCheck = line.getLineText().replace(keyStartBillNo2, "").trim();
                        bill = findBillByCode(keyCheck);
                        receipt.setReceiptId(keyCheck);
                    }
                    checkReceipt = true;

                    if (bill != null) {
                        break;
                    }

                } else if (line.getLineText().contains(keyStartListItem) || line.getLineText().contains(keyStartListItem2) || line.getLineText().contains(keyStartListItem3)) {
                    logger.info("[start]: " + line.getLineText());
                    checkListItems = true;
                } else if (line.getLineText().contains(keyEndListItem) || line.getLineText().contains(keyEndListItem2)) {
                    checkListItems = false;
                    logger.info("[end]: " + line.getLineText());
                }

                if (checkListItems) {
                    if (!isInteger(line.getLineText()) && !line.getLineText().contains(keyCheckVat) && (!line.getLineText().contains(keyStartListItem) && !line.getLineText().contains(keyStartListItem2) && !line.getLineText().contains(keyStartListItem3))) {
                        int quantity = 0;
                        double price = 0;
                        if (isInteger(lineContinute.getLineText()) && !lineContinute.getLineText().contains(keyCheckMonney) && !lineContinute.getLineText().contains(keyCheckMonney2) && !lineContinute.getLineText().contains(keyCheckMonney3)) {
                            quantity = Integer.parseInt(lineContinute.getLineText());
                        } else if (isInteger(lineStep2.getLineText()) && !lineStep2.getLineText().contains(keyCheckMonney) && !lineStep2.getLineText().contains(keyCheckMonney2) && !lineStep2.getLineText().contains(keyCheckMonney3)) {
                            quantity = Integer.parseInt(lineStep2.getLineText());
                        } else if (isInteger(lineStep3.getLineText()) && !lineStep3.getLineText().contains(keyCheckMonney) && !lineStep3.getLineText().contains(keyCheckMonney2) && !lineStep3.getLineText().contains(keyCheckMonney3)) {
                            quantity = Integer.parseInt(lineStep3.getLineText());
                        } else if (isInteger(lineStep4.getLineText()) && !lineStep4.getLineText().contains(keyCheckMonney) && !lineStep4.getLineText().contains(keyCheckMonney2) && !lineStep4.getLineText().contains(keyCheckMonney3)) {
                            quantity = Integer.parseInt(lineStep4.getLineText());
                        }

                        if (lineContinute.getLineText().contains(keyCheckVat)) {
                            price = convertToDouble(lineStep2.getLineText());
                        } else if (lineStep2.getLineText().contains(keyCheckVat)) {
                            price = convertToDouble(lineStep3.getLineText());
                        }

                        ItemReceipt itemReceipt = new ItemReceipt();
                        itemReceipt.setItemName(line.getLineText());
                        itemReceipt.setQuantity(quantity);
                        itemReceipt.setPrice(price);
                        logger.info("|>>---------------------------->>|");
                        logger.info("SP: " + itemReceipt.getItemName());
                        logger.info("Quantity: " + itemReceipt.getQuantity());
                        logger.info("Price: " + itemReceipt.getPrice());
                        logger.info("|<<----------------------------<<|");
                        itemReceiptList.add(itemReceipt);
                    }
                }
            }
            if (!checkReceipt) {
                receipt.setStatus(EnumManager.Billtatus.NOCONTENT);
            }
            if (bill != null) {
                receipt.setStatus(EnumManager.Billtatus.DISABLE);
            }

            List<ItemPoint> listItem = new ArrayList<>();
            receipt.setListItems(itemReceiptList);
            ItemPoint item = new ItemPoint();
            String arrayItem = "";
            if (!receipt.getListItems().isEmpty()){
                String text = ",";
                for (int i = 0; i < receipt.getListItems().size(); i++) {
                    if(i == receipt.getListItems().size()-1){
                        text = "";
                    }
                    Optional<Integer> point = productEventRepo.getPointByProductName(receipt.getListItems().get(i).getItemName(), eventId);
                    if (point.isPresent()) {

                        item.setName(receipt.getListItems().get(i).getItemName());
                        item.setQuantity(receipt.getListItems().get(i).getQuantity());
                        item.setPoint(point);
                        listItem.add(item);
                        totalPoint += point.get() * receipt.getListItems().get(i).getQuantity();
                        arrayItem += receipt.getListItems().get(i).getItemName() + "(" + receipt.getListItems().get(i).getQuantity() + ")"+text;
                    }
                }
        }
            logger.info("arrayItem: " + arrayItem);
            receipt.setArrayDataItem(arrayItem);
            receipt.setListDataItems(listItem);

            if(totalPoint >0){
                receipt.setTotalPoint(totalPoint);
                receipt.setStatus(EnumManager.Billtatus.APPROVE);


            }
        }
        return receipt;
    }

    @Override
    public void processFileScan(Receipt receipt, MultipartFile file, Integer customerId, Integer eventId) {
        Bill bill = new Bill();
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event","EventId",+ eventId));
        Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId,eventId);
        CustomerPoint customerPoint;
        if (customerPointOptional.isPresent()) {
            customerPoint = customerPointOptional.get();
            int currentTotalPoint = customerPoint.getPoint() + receipt.getTotalPoint();
            customerPoint.setPoint(currentTotalPoint);
        } else {
            Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer","CustomerId",customerId));

            customerPoint = new CustomerPoint();
            customerPoint.setCustomer(customer);
            customerPoint.setEvent(event);
            customerPoint.setPoint(receipt.getTotalPoint());
        }
        customerPointRepo.save(customerPoint);
        Map data = cloudinaryService.upload(file);
        bill.setBillCode(receipt.getReceiptId());
        bill.setImage(String.valueOf(data.get("secure_url")));
        bill.setCreateDate(LocalDateTime.now());
        bill.setPoint(receipt.getTotalPoint());
        bill.setCustomerId(customerId);
        bill.setInfomation(receipt.getInfomation());
        bill.setBuyDate(receipt.getBuyDate());
        bill.setEvent(event);
        bill.setDeleteFlag(false);
        bill.setStatus(receipt.getStatus());
        bill.setArrayItem(receipt.getArrayDataItem());
        bill.setEventName(eventRepo.findById(eventId).get().getName());
        savebill(bill);

//        return new ResponseEntity<>("Scan bill successfully, your current score is: "+ customerPoint.getPoint(), HttpStatus.OK);
    }



    private boolean isInteger(String str) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(str);
            int value = number.intValue();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private double convertToDouble(String str) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(str);
            double value = number.doubleValue();
            return value;
        } catch (ParseException e) {
            return 0;
        }
    }
}
