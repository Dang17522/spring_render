package com.zalo.Spring_Zalo.ServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.Spring_Zalo.Controller.CustomerEventController;
import com.zalo.Spring_Zalo.DTO.DateConverterDTO;
import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.*;
import com.zalo.Spring_Zalo.Response.ReplacementResult;

import com.zalo.Spring_Zalo.Service.CloudinaryService;
import jakarta.transaction.Transactional;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zalo.Spring_Zalo.Service.CustomerEventService;
import com.zalo.Spring_Zalo.request.FileStorageManager;

@Service
public class CustomerEventServiceImpl implements CustomerEventService {
    @Autowired
    private ProductEventRepo productEventRepo;

    @Autowired
    private CustomerRewardRepo customerRewardRepo;
    @Autowired
    private CustomerPointRepo   customerPointRepo;

    @Autowired
    private BillServiceImpl billServiceImpl;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private BillRepo billRepo;

    @Autowired
    private CustomerEventRepo customerEventRepo;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    private final String storageDirectory = FileStorageManager.getStorageDirectory();

    private static final Logger logger = LoggerFactory.getLogger(CustomerEventServiceImpl.class);
    /**
     * @param result
     * @param currentLocale
     * @return
     */
    @Override
    public Object ScanResult(String result, Locale currentLocale,Integer eventId,Integer customerId){
        Bill bill = new Bill();
        String billsaveCode = "";
        LocalDate billsaveDate = LocalDate.now();
        ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);
        String billingDate = keyWord.getString("keyBillingDate");
        String startBillNo = keyWord.getString("keyStartBillNo");
        String startBillNo2 = keyWord.getString("keyStartBillNo2");        
        String endListItem = keyWord.getString("keyEndListItem");//="Cong tién hang"
        String startListItem = keyWord.getString("keyStartListItem");
        String endListItem2= keyWord.getString("keyEndListItem2");
        List<String> lines = Arrays.asList(result.split("\n"));
        List<Item> items = new ArrayList<>();
        ItemReturn itemReturn = new ItemReturn();
		boolean isListItem = false;
        boolean isBillNo = false ;
        
        int totalPoint = 0;
        
        logger.info(startListItem);
        // logger.info("Scanning");
        // create a loop to Approve each line
		for (String line : lines) {
            if (line.equals(billingDate)) {
				logger.info(billingDate);
                continue;
			}
            if(line.equals(startBillNo)|| line.equals(startBillNo2)){
                isBillNo = true;
                continue;
            }
            if(isBillNo){
                bill = billServiceImpl.findBillByCode(line);
                logger.info("bill->>"+bill);
                if(bill != null ){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("This bill already Used! ");
                } 
                billsaveDate = LocalDate.now();
                logger.info("code Line =>> "+  line );
                billsaveCode = line;
                

                isBillNo = false;
                continue;
            }
            if(line.contains(startListItem)){
                isListItem = true;
                continue;
            }
            if(isListItem){
                logger.info("Process bill 1 ");
                // found list product in bill and begin to read infomation each product to get list item  
                processListItemOCR(lines,line,items,itemReturn,endListItem,endListItem2,isListItem,eventId,totalPoint,currentLocale);
                break;
            }
            
        }  
        if(items.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item List is empty , please check your recive! ");

        }
        logger.info("->>DAte "+ billsaveDate);
        logger.info("->> Code "+ billsaveCode);
        Bill billSave = new Bill();
        billSave.setBillCode(billsaveCode);
        //logger.info("->> Code "+ bill.getBillCode());
        billSave.setScanDate(billsaveDate);
        billServiceImpl.savebill(billSave);

        
            Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId,eventId);
            CustomerPoint customerPoint;
            if (customerPointOptional.isPresent()) {
                customerPoint = customerPointOptional.get();
                int currentTotalPoint = customerPoint.getPoint() + itemReturn.getTotalPoint();
                customerPoint.setPoint(currentTotalPoint);
            } else {
                Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer","CustomerId",customerId));
                Event event = eventRepo.findById(3).orElseThrow(() -> new ResourceNotFoundException("Event","EventId",3));
                // Nếu không tìm thấy đối tượng CustomerPoint, bạn có thể tạo mới và  cộng điểm cho nó
                customerPoint = new CustomerPoint();
                customerPoint.setCustomer(customer);
                customerPoint.setEvent(event);
                customerPoint.setPoint(itemReturn.getTotalPoint());
            }
        
            customerPointRepo.save(customerPoint);
        return itemReturn;
    }
    private void processListItemOCR(List<String> lines,String line ,List<Item> items,ItemReturn itemReturn,String endListItem,String endListItem2,  Boolean isListItem,Integer eventId,int totalPoint,Locale currentLocale) {
        List<String> subLines = lines.subList(lines.indexOf(line), lines.size());

        ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);
        String cafeSua_Fake = keyWord.getString("keyCafeSua_fake");
        String cafeSua = keyWord.getString("keyCafeSua");
        String cafeDen_fake = keyWord.getString("keyCafeDen_fake");
        String cafeDen = keyWord.getString("keyCafeDen");
        String BeafSteak_fake= keyWord.getString("keyBeefStake_fake");
        String BeafSteak= keyWord.getString("keyBeefStake");
        String VietVangPure_fake= keyWord.getString("keyVietVangPure_fake");
        String VietVangPure= keyWord.getString("keyVietVangPure");
        String GoldenVietVang_fake= keyWord.getString("keyGoldenVietVang_fake");
        String GoldenVietVang= keyWord.getString("keyGoldenVietVang");
        String VietCoffee_fake= keyWord.getString("keyVietCoffee_fake");
        String VietCoffee= keyWord.getString("keyVietCoffee");
        String Soul_fake= keyWord.getString("keySoul_fake");
        String Soul= keyWord.getString("keySoul");
        String ComboPremium_fake= keyWord.getString("keyComboPremium_fake");
        String ComboPremium= keyWord.getString("keyComboPremium");
        //Store in hashmap 
        Map<String, String> replacements = new HashMap<>();
        replacements.put(cafeSua_Fake, cafeSua);
        replacements.put(cafeDen_fake, cafeDen);
        replacements.put(ComboPremium_fake, ComboPremium);
        replacements.put(BeafSteak_fake,BeafSteak);
        replacements.put(GoldenVietVang_fake,GoldenVietVang);
        replacements.put(Soul_fake,Soul);
        replacements.put(VietCoffee_fake,VietCoffee);
        replacements.put(VietVangPure_fake,VietVangPure);
        for (int i = subLines.indexOf(line); i < subLines.size(); i+=2) {
            String currentLine = subLines.get(i);

            if (currentLine.contains(endListItem)|| currentLine.contains(endListItem2)) {
				isListItem = false;// change flag
                logger.info(currentLine);
                break;
			}
            if (isListItem) {
                String name = subLines.get(i);
                logger.info("name1:  "+ name);
                int quantity = Integer.parseInt(subLines.get(i + 1).split(" ")[0]);

                if (replacements.containsKey(name)) {
                    name = replacements.get(name);
                }
                logger.info("name2:  "+ name);
                Item item = new Item(name.trim(), quantity);
                logger.info("item: "+item);
                String productName = item.getName();
                logger.info("product "+ productName);
                Optional<Integer> point = productEventRepo.getPointByProductName(productName, eventId);
                
                if (point.isPresent()) {
                    logger.info("Point "+ point.get());
                    totalPoint += point.get(); // Thêm điểm vào totalPoint nếu point có giá trị
                    item = new Item(name.trim(), quantity);
                    items.add(item);
                    itemReturn.setItems(items);
                    itemReturn.setTotalPoint(totalPoint);
                    
                }
			}    
        }
    }



    @Override
    public Object ScanResultOCR(String result, Locale currentLocale,Integer eventId,Integer customerId){
        Bill bill = new Bill();
        String billsaveCode = "";
        LocalDate billsaveDate = LocalDate.now();
        ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);
        //String biling date 
        String billingDate = keyWord.getString("keyBillingDate");
        //String billing No 
        String startBillNo = keyWord.getString("keyStartBillNo");
        String startBillNo2 = keyWord.getString("keyStartBillNo2"); 
        String startBillNo3 = keyWord.getString("keyStartBillNo3");         
        // String start List Item
        String startListItem = keyWord.getString("keyStartListItem");
        String startListItem1 = keyWord.getString("keyStartListItem1");
        //String end List Item 
        String endListItem = keyWord.getString("keyEndListItem");//="Cong tién hang"
        String endListItem2= keyWord.getString("keyEndListItem2");
        String endListItem3= keyWord.getString("keyEndListItem3");//Tổng tiền đơn hàng
        
        // Device into Lines (list)
        List<String> lines = Arrays.asList(result.split("\n"));
        List<Item> items = new ArrayList<>();
        ItemReturn itemReturn = new ItemReturn();
		boolean isListItem = false;
        boolean isBillNo = false ;
        
        int totalPoint = 0;
        
        logger.info(startListItem);
        // logger.info("Scanning");
        // create a loop to Approve each line
        for (String line : lines) {
            if (line.equals(billingDate)) {
				logger.info(billingDate);
                continue;
			}
            if(line.equals(startBillNo)|| line.equals(startBillNo2)||line.equals(startBillNo3)){
                isBillNo = true;
                continue;
            }
            if(isBillNo){
                bill = billServiceImpl.findBillByCode(line);
                logger.info("bill->>"+bill);
                if(bill != null ){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("This bill already Used! ");
                } 
                billsaveDate = LocalDate.now();
                logger.info("code Line =>> "+  line );
                billsaveCode = line;
                

                isBillNo = false;
                continue;
            }
            if(line.contains(startListItem)||line.contains(startListItem1)){
                isListItem = true;
                continue;
            }
            if(isListItem){
                logger.info("Process bill 2 ");
                // found list product in bill and begin to read infomation each product to get list item  
                processListItem(lines,line,items,itemReturn,endListItem,endListItem2,endListItem3,isListItem,eventId,totalPoint,currentLocale);
                break;
            }
            
        }  
        if(items.isEmpty()){
          
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item List is empty , please check your recive! ");

        }
        logger.info("->>DAte "+ billsaveDate);
        logger.info("->> Code "+ billsaveCode);
        Bill billSave = new Bill();
        billSave.setBillCode(billsaveCode);
        //logger.info("->> Code "+ bill.getBillCode());
        billSave.setScanDate(billsaveDate);
        //logger.info("->> Date "+ bill.getScanDate());
        billServiceImpl.savebill(billSave);

        
            Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId,eventId);
            CustomerPoint customerPoint;
            if (customerPointOptional.isPresent()) {
                customerPoint = customerPointOptional.get();
                int currentTotalPoint = customerPoint.getPoint() + itemReturn.getTotalPoint();
                customerPoint.setPoint(currentTotalPoint);
            } else {
                Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer","CustomerId",customerId));
                Event event = eventRepo.findById(3).orElseThrow(() -> new ResourceNotFoundException("Event","EventId",3));
                // Nếu không tìm thấy đối tượng CustomerPoint, bạn có thể tạo mới và  cộng điểm cho nó
                customerPoint = new CustomerPoint();
                customerPoint.setCustomer(customer);
                customerPoint.setEvent(event);
                customerPoint.setPoint(itemReturn.getTotalPoint());
            }
        
            customerPointRepo.save(customerPoint);
        return itemReturn;
    }
    private void processListItem(List<String> lines,String line ,List<Item> items,ItemReturn itemReturn,String endListItem,String endListItem2,String endListItem3,  Boolean isListItem,Integer eventId,int totalPoint,Locale currentLocale) {
        List<String> subLines = lines.subList(lines.indexOf(line), lines.size());
        ResourceBundle keyWord = ResourceBundle.getBundle("keyWordOg", currentLocale);
        // ResourceBundle keyWord_JP = ResourceBundle.getBundle("keyWord", currentLocale);
        //key change if wrong text value
        String cafeSua_Fake = keyWord.getString("keyCafeSua_fake");
        String cafeSua = keyWord.getString("keyCafeSua");
        String cafeDen_fake = keyWord.getString("keyCafeDen_fake");
        String cafeDen = keyWord.getString("keyCafeDen");
        String BeafSteak_fake= keyWord.getString("keyBeefStake_fake");
        String BeafSteak= keyWord.getString("keyBeefStake");
        String VietVangPure_fake= keyWord.getString("keyVietVangPure_fake");
        String VietVangPure= keyWord.getString("keyVietVangPure");
        String GoldenVietVang_fake= keyWord.getString("keyGoldenVietVang_fake");
        String GoldenVietVang= keyWord.getString("keyGoldenVietVang");
        String VietCoffee_fake= keyWord.getString("keyVietCoffee_fake");
        String VietCoffee= keyWord.getString("keyVietCoffee");
        String Soul_fake= keyWord.getString("keySoul_fake");
        String Soul= keyWord.getString("keySoul");
        String ComboPremium_fake= keyWord.getString("keyComboPremium_fake");
        String ComboPremium= keyWord.getString("keyComboPremium");
        String VietVangShare_fake= keyWord.getString("keyVietVangShare_fake");
        String VietVangShare= keyWord.getString("keyVietVangShare");
        String VietVangTalentshow_fake= keyWord.getString("keyVietVangTalentshow_fake");
        String VietVangTalentshow= keyWord.getString("keyVietVangTalentshow");
        //currency
        String currency = keyWord.getString("keyVietNamCurrency");

        //Store in hashmap 
        Map<String, String> replacements = new HashMap<>();
        replacements.put(cafeSua_Fake, cafeSua);
        replacements.put(cafeDen_fake, cafeDen);
        replacements.put(ComboPremium_fake, ComboPremium);
        replacements.put(BeafSteak_fake,BeafSteak);
        replacements.put(GoldenVietVang_fake,GoldenVietVang);
        replacements.put(Soul_fake,Soul);
        replacements.put(VietCoffee_fake,VietCoffee);
        replacements.put(VietVangPure_fake,VietVangPure);
        replacements.put(VietVangShare_fake,VietVangShare);
        replacements.put(VietVangTalentshow_fake,VietVangTalentshow);
        
        for (int i = subLines.indexOf(line); i < subLines.size(); i+=2) {
            String currentLine = subLines.get(i);
            String nextLine = subLines.get(i+1);
            Item item = new Item("",1);
            String beforeLine=currency;
            if(i!=0){
                beforeLine = subLines.get(i-1);
            }
            //boolean IsMenu = false;
            if (currentLine.contains(endListItem)|| currentLine.contains(endListItem2)|| currentLine.contains(endListItem3)||beforeLine.contains(endListItem3)||nextLine.contains(endListItem3)) {
				isListItem = false;// change flag
                logger.info(currentLine);
                break;
			}
            if (isListItem) {
                    item.setQuantity(1);
                    if(isInteger(nextLine)){
                        item.setName(currentLine);
                        item.setQuantity(Integer.parseInt(nextLine));
                        logger.info("item: "+item);
                        String productName = item.getName();
                        logger.info("product "+ productName);
                        Optional<Integer> point = productEventRepo.getPointByProductName(productName, eventId);
                        logger.info(">>Point: "+  point);
                        if (point.isPresent()) {
                            logger.info("Point "+ point.get());
                            totalPoint = totalPoint+ point.get()* item.getQuantity(); 
                            items.add(item);
                            itemReturn.setItems(items);
                            itemReturn.setTotalPoint(totalPoint);
                            
                        }  
                    } 
                    // continue;
                
                    if(isInteger(currentLine)){
                        item.setName(beforeLine);
                        item.setQuantity(Integer.parseInt(currentLine));
                        logger.info("item: "+item);
                        String productName = item.getName();
                        logger.info("product "+ productName);
                        Optional<Integer> point = productEventRepo.getPointByProductName(productName, eventId);
                        logger.info(">>Point: "+  point);
                        if (point.isPresent()) {
                            logger.info("Point "+ point.get());
                            totalPoint = totalPoint + point.get() * item.getQuantity(); // Thêm điểm vào totalPoint nếu point có giá trị
                            items.add(item);
                            itemReturn.setItems(items);
                            itemReturn.setTotalPoint(totalPoint);
                            
                        }
                    }     
            }          
			    
        }
    }

    @Override
    public Object ScanResultOCRMapping(List<Line> linesList, Locale currentLocale,Integer eventId,Integer customerId){
        ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);
        String billsaveCode = "";
        LocalDate billsaveDate = LocalDate.now();
        //String biling date 
        String billingDate = keyWord.getString("keyBillingDate");
        //String billing No 
        String startBillNo = keyWord.getString("keyStartBillNo");
        String startBillNo2 = keyWord.getString("keyStartBillNo2"); 
        String startBillNo3 = keyWord.getString("keyStartBillNo3");         
        // // String start List Item
        // String startListItem = keyWord.getString("keyStartListItem");
        // String startListItem1 = keyWord.getString("keyStartListItem1");
        // //String end List Item 
        // String endListItem = keyWord.getString("keyEndListItem");//="Cong tién hang"
        // String endListItem2= keyWord.getString("keyEndListItem2");
        // String endListItem3= keyWord.getString("keyEndListItem3");//Tổng tiền đơn hàng
        //Bill
        Bill bill = new Bill();
        int totalPoint= 0;
        List<ItemPoint> items = new ArrayList<>();
        ItemPointReturn itemReturn = new ItemPointReturn();
        if(linesList.isEmpty()){
           return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item List is empty , please check your recive! ");
        }
        DateConverterDTO convert = new DateConverterDTO();
        
        
        for ( int i = 0; i < linesList.size(); i+=2 ){
            String currentWord = linesList.get(i).getLineText();
            
                logger.info("CurrentText: "+currentWord);
                String nextWord="";
                while(i < linesList.size()){
                    nextWord = linesList.get(i+1).getLineText();
                }
                

                if(currentWord.contains(billingDate)){
                    billsaveDate = convert.convertToDate(currentWord);

                   if (billsaveDate != null) {
                       bill.setScanDate(billsaveDate);
                   
                   } else if(nextWord.contains(billingDate)){
                       billsaveDate = convert.convertToDate(nextWord);
  
                      if (billsaveDate != null) {
                          bill.setScanDate(billsaveDate);
                      }
                   }
                }

                if(currentWord.equals(startBillNo)|| currentWord.equals(startBillNo2)||currentWord.equals(startBillNo3)){
                    billNoProcess(currentWord,bill);
                    continue;
                }
                if(nextWord.equals(startBillNo)|| nextWord.equals(startBillNo2)||nextWord.equals(startBillNo3)){
                    billNoProcess(currentWord,bill);
                    continue;
                }
                
                
                
                if(isInteger(currentWord)){
                    String beforeWord = linesList.get(i-1).getLineText();
                    ItemPoint item =CheckIfProductName(beforeWord,currentWord,eventId); 
                    if(item!= null){
                        items.add(item);
                        totalPoint += item.getPoint().orElse(1) * item.getQuantity();
                    }
                    continue;
                }else{
                    ItemPoint item = CheckIfProductName(currentWord,nextWord,eventId);
                    if(item!= null){
                        items.add(item);
                        totalPoint += item.getPoint().orElse(1) * item.getQuantity();
                    }
                }
                logger.info("->>DAte "+ billsaveDate);
                logger.info("->> Code "+ billsaveCode);
                Bill billSave = new Bill();
                billSave.setBillCode(billsaveCode);
                //logger.info("->> Code "+ bill.getBillCode());
                billSave.setScanDate(billsaveDate);
                //logger.info("->> Date "+ bill.getScanDate());
                billServiceImpl.savebill(billSave);
            }        

            
        if (items.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item List is empty , please check your recive! ");
        }else{
            itemReturn.setItems(items);
            itemReturn.setTotalPoint(totalPoint);

            return ResponseEntity.ok(itemReturn);
        }
        
}
    

public boolean billNoProcess(String codebill ,Bill bill){
    if(bill.getBillCode() !=  null){
        bill = billServiceImpl.findBillByCode(codebill);
        if(bill != null ){
            return false;
        } 
    }
    return true;
}


public ItemPoint CheckIfProductName(String currentWord ,String nextWord,Integer eventId){
    String keyCheckMonney = "đ";
    int quantity = 0;
    if(isInteger(nextWord) && !nextWord.contains(keyCheckMonney)){
        logger.info("nextWord: "+nextWord);
        quantity = Integer.parseInt(nextWord);

        logger.info("currentWord: "+currentWord);
        Optional<Integer> point = productEventRepo.getPointByProductName(currentWord, eventId);
        //  Optional<Integer> point = productEventRepo.getPointByProductName(productName, eventId);
        logger.info("Point >> " + point + ": "+currentWord +": "+quantity);
        ItemPoint item = new ItemPoint();
        if (point.isPresent()) {
            logger.info("Point "+ point.get());
            item.setName(currentWord);
            item.setQuantity(quantity);
            item.setPoint(point);
            return item;
        }
    }
    return null;
}
	/**
	 * methob use to check if input is an integer or not
	 * 
	 * @param String    
	 * @return true if it's an Integer || false if It's not
	 */
	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;                                                                   
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * methob use to check if input is a double or not
	 * 
	 * @param String
	 * @return true if it's a Double || false if It's not
	 */
	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str.replace(",", ""));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
    }

public ReplacementResult keyexchange(String text, Locale currentLocale){
    ResourceBundle keyWord = ResourceBundle.getBundle("keyWordOg", currentLocale);
        // ResourceBundle keyWord_JP = ResourceBundle.getBundle("keyWord", currentLocale);
        //key change if wrong text value
        String cafeSua_Fake = keyWord.getString("keyCafeSua_fake");
        String cafeSua = keyWord.getString("keyCafeSua");
        String cafeDen_fake = keyWord.getString("keyCafeDen_fake");
        String cafeDen = keyWord.getString("keyCafeDen");
        String BeafSteak_fake= keyWord.getString("keyBeefStake_fake");
        String BeafSteak= keyWord.getString("keyBeefStake");
        String VietVangPure_fake= keyWord.getString("keyVietVangPure_fake");
        String VietVangPure= keyWord.getString("keyVietVangPure");
        String GoldenVietVang_fake= keyWord.getString("keyGoldenVietVang_fake");
        String GoldenVietVang= keyWord.getString("keyGoldenVietVang");
        String VietCoffee_fake= keyWord.getString("keyVietCoffee_fake");
        String VietCoffee= keyWord.getString("keyVietCoffee");
        String Soul_fake= keyWord.getString("keySoul_fake");
        String Soul= keyWord.getString("keySoul");
        String ComboPremium_fake= keyWord.getString("keyComboPremium_fake");
        String ComboPremium= keyWord.getString("keyComboPremium");
        String VietVangShare_fake= keyWord.getString("keyVietVangShare_fake");
        String VietVangShare= keyWord.getString("keyVietVangShare");
        String VietVangTalentshow_fake= keyWord.getString("keyVietVangTalentshow_fake");
        String VietVangTalentshow= keyWord.getString("keyVietVangTalentshow");
        //currency
        String currency = keyWord.getString("keyVietNamCurrency");
        
        Map<String, String> replacements = new HashMap<>();
        replacements.put(cafeSua_Fake, cafeSua);
        replacements.put(cafeDen_fake, cafeDen);
        replacements.put(ComboPremium_fake, ComboPremium);
        replacements.put(BeafSteak_fake,BeafSteak);
        replacements.put(GoldenVietVang_fake,GoldenVietVang);
        replacements.put(Soul_fake,Soul);
        replacements.put(VietCoffee_fake,VietCoffee);
        replacements.put(VietVangPure_fake,VietVangPure);
        replacements.put(VietVangShare_fake,VietVangShare);
        replacements.put(VietVangTalentshow_fake,VietVangTalentshow);
        boolean replacementOccurred = false;
       
    // Iterate through the map and replace keys in the text
    for (Map.Entry<String, String> entry : replacements.entrySet()) {
        if (text.contains(entry.getKey())) {
            text = text.replace(entry.getKey(), entry.getValue());
            replacementOccurred = true;
        }
    }

    return new ReplacementResult(text, replacementOccurred);
}



public ProcessingState whatToDo(String text, Locale currentLocale) {

    ResourceBundle keyWord = ResourceBundle.getBundle("keyWord", currentLocale);

   
        // Check for billing date
        logger.info("textCheck: "+text);
        logger.info("keyBillingDate: " +keyWord.getString("keyBillingDate"));
        if (text.contains(keyWord.getString("keyBillingDate"))) {
            logger.info("BILLING_DATE: "+text);
            return ProcessingState.BILLING_DATE;
        }

        // Check for start bill numbers
        logger.info("keyStartBillNo: " +keyWord.getString("keyStartBillNo"));
        if (text.contains(keyWord.getString("keyStartBillNo")) ||
            text.contains(keyWord.getString("keyStartBillNo2")) ||
            text.contains(keyWord.getString("keyStartBillNo3"))) {
            logger.info("BILLING_NO: "+text);
            return ProcessingState.BILLING_NO;
        }

        // Check for start list items
        logger.info("keyStartListItem: " +keyWord.getString("keyStartBillNo"));
        if (text.contains(keyWord.getString("keyStartListItem")) ||
            text.contains(keyWord.getString("keyStartListItem1"))) {
            logger.info("LIST_ITEM: "+text);
            return ProcessingState.LIST_ITEM;
        }
        logger.info("keyEndListItem: " +keyWord.getString("keyEndListItem"));
        if(text.contains(keyWord.getString("keyEndListItem")) ||
        text.contains(keyWord.getString("keyEndListItem2")) ||
        text.contains(keyWord.getString("keyEndListItem3"))){
            logger.info("LIST_END: "+text);
            return ProcessingState.LIST_END;
        }

        return ProcessingState.NONE;
    
}
public enum ProcessingState {
    NONE, BILLING_DATE, BILLING_NO, LIST_ITEM, LIST_END
}


@Override
public Object ProcessingLine(List<Line> lines, Locale currentLocale, Integer customerId, Integer eventId) {
    Bill bill = new Bill();
    LocalDate scannedDate = LocalDate.now();
    DateConverterDTO dateConverter = new DateConverterDTO();
    Boolean isListItem = false;
    int totalPoint= 0;
    List<ItemPoint> items = new ArrayList<>();
    ItemPointReturn itemReturn = new ItemPointReturn();
    ItemPoint item = new ItemPoint();
    ProcessingState currentState = ProcessingState.NONE;
    for (Line line : lines) {
        currentState = whatToDo(line.getLineText(), currentLocale);
        switch (currentState) {
            case BILLING_DATE:
                scannedDate = dateConverter.convertToDate(line.getLineText());
                if (scannedDate != null) {
                    bill.setScanDate(scannedDate);
                } else {
                    System.err.println("Error Cover Scandate !");
                }
                break;
            case BILLING_NO:
                bill.setBillCode(line.getLineText());
                billNoProcess(bill.getBillCode(),bill);
                logger.info("BillCode: "+bill.getBillCode());
                break;
            case LIST_ITEM:
                isListItem = true;
                break;
            case LIST_END:
                isListItem = false;
            case NONE:
                break;
        }
        if(isListItem){
            ReplacementResult result = keyexchange(line.getLineText(),currentLocale);
            if(result.isReplacementOccurred()){
                item.setName(line.getLineText());
                item = CheckIfProductName(line.getLineText() ,line.getLineText(),eventId);
                if(item!= null){
                    items.add(item);
                    totalPoint += item.getPoint().orElse(1) * item.getQuantity();
                }
                items.add(item);
            }
        }
        if (items.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item List is empty , please check your recive! ");
        }else{

            itemReturn.setItems(items);
            itemReturn.setTotalPoint(totalPoint);

            return ResponseEntity.ok(itemReturn);
        }
        
        
    }
    billServiceImpl.savebill(bill);
    throw new UnsupportedOperationException("Unimplemented method 'ProcessingLine'");
}

public ResponseEntity<String> processFile(MultipartFile file, String json, Locale currentLocale, Integer customerId, Integer eventId) {
    logger.info("Processing data !");
    Bill bill = new Bill();
    LocalDate scannedDate = LocalDate.now();
    Boolean isListItem = false;
    Boolean isCheckBillUse = true;
    int totalPoint = 0;
    List<ItemPoint> items = new ArrayList<>();
    ItemPointReturn itemReturn = new ItemPointReturn();
    DateConverterDTO dateConverter = new DateConverterDTO();

    if (json == null || json.isEmpty()) {
        logger.info("Invalid JSON!");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        ParsedResult parsedResult = objectMapper.readValue(json, ParsedResult.class);
        List<Line> lineTextList = parsedResult.getParsedResults().get(0).getTextOverlay().getLines();
        logger.info("lineTextList: "+lineTextList);
        // for (Line line : lineTextList) {
        logger.info("Scancode:");
            for (int i = 0; i < lineTextList.size(); i++) {
                Line line = lineTextList.get(i);
                Line lineContinute = line;
                if(i < lineTextList.size() -1 ){
                    lineContinute = lineTextList.get(i + 1);
                }
            // List<Word> words = line.getWords();
            ProcessingState currentState = ProcessingState.NONE;
            currentState = whatToDo(line.getLineText(), currentLocale);
//                bill.setScanDate(LocalDate.now());
                switch (currentState) {

//                    case BILLING_DATE:
//                        scannedDate = dateConverter.convertToDate(line.getLineText());
//                        if (scannedDate != null) {
//                            bill.setScanDate();
//                        } else {
//                            System.err.println("Error Cover Scandate!");
//                        }
//                        break;
                    case BILLING_NO:
//                        billNoProcess(line.getLineText(), bill);
                        isCheckBillUse = billNoProcess(line.getLineText(), bill);
                        bill.setBillCode(line.getLineText());
                        bill.setScanDate(scannedDate);
                        bill.setStatus(EnumManager.Billtatus.APPROVE);
                        logger.info("BillCode: "+bill.getBillCode());
                        break;
                    case LIST_ITEM:
                        isListItem = true;
                        break;
                    case LIST_END:
                        isListItem = false;
                    case NONE:
                        break;
                }
                if (isListItem && isCheckBillUse) {
                    logger.info("listItem: "+line.getLineText().trim());
                        ItemPoint item = CheckIfProductName(line.getLineText().trim(), lineContinute.getLineText(), eventId);
                        if (item != null) {
                            items.add(item);
                            totalPoint += item.getPoint().orElse(1) * item.getQuantity();
                        }
                }
            }
        if(!isCheckBillUse){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This bill already Used! ");
        }
        if (items.isEmpty()) {
//            saveBillImage(file,eventId, customerId, EnumManager.Billtatus.NOCONTENT);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            itemReturn.setItems(items);
            itemReturn.setTotalPoint(totalPoint);
            Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId,eventId);
            CustomerPoint customerPoint;
            Event event = eventRepo.findById(3).orElseThrow(() -> new ResourceNotFoundException("Event","EventId",3));
            if (customerPointOptional.isPresent()) {
                customerPoint = customerPointOptional.get();
                int currentTotalPoint = customerPoint.getPoint() + itemReturn.getTotalPoint();
                customerPoint.setPoint(currentTotalPoint);
            } else {
                Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer","CustomerId",customerId));

                // Nếu không tìm thấy đối tượng CustomerPoint, bạn có thể tạo mới và  cộng điểm cho nó
                customerPoint = new CustomerPoint();
                customerPoint.setCustomer(customer);
                customerPoint.setEvent(event);
                customerPoint.setPoint(itemReturn.getTotalPoint());
            }
            customerPointRepo.save(customerPoint);
            bill.setPoint(itemReturn.getTotalPoint());
            bill.setCustomerId(customerId);
            bill.setEvent(event);
            bill.setEventName(eventRepo.findById(eventId).get().getName());
            billServiceImpl.savebill(bill);
       

//            return ResponseEntity.ok(itemReturn);
            return new ResponseEntity<>("Scan bill successfully, your current score is: "+ customerPoint.getPoint(),HttpStatus.OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
    // for (Word word : words) {}
}

    public ResponseEntity<String> processFileScan(Receipt receipt, MultipartFile file, Integer customerId, Integer eventId) {
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
        bill.setBillCode(receipt.getReceiptId());
        bill.setCreateDate(LocalDateTime.now());
        bill.setPoint(receipt.getTotalPoint());
        bill.setCustomerId(customerId);
        bill.setEvent(event);
        bill.setDeleteFlag(false);
        bill.setStatus(receipt.getStatus());
        bill.setEventName(eventRepo.findById(eventId).get().getName());
        billServiceImpl.savebill(bill);

        return new ResponseEntity<>("Scan bill successfully, your current score is: "+ customerPoint.getPoint(),HttpStatus.OK);
    }

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public void saveBillImage(MultipartFile file, int eventId, int customerId, Receipt receipt) {

        logger.info("process Save Bill Image");
//            File folder = new File(System.getProperty("user.dir") + storageDirectory);
//            if(!folder.exists()){
//                folder.mkdirs();
//            }
        Map data = cloudinaryService.upload(file);
        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer","CustomerId",customerId));
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event","EventId",eventId));
        FileStorageManager fileStorageManager = new FileStorageManager(storageDirectory);
//            String fileName = fileStorageManager.storeFile(file, eventId, customerId, LocalDateTime.now());
        String codeName = fileStorageManager.generateCodeName(eventId, customerId);
        Bill bill = new Bill();
        bill.setBillCode(receipt.getReceiptId());
        bill.setImage(String.valueOf(data.get("secure_url")));
        bill.setInfomation(receipt.getInfomation());
        bill.setBuyDate(receipt.getBuyDate());
        bill.setCreateDate(LocalDateTime.now());
        bill.setStatus(receipt.getStatus());
        bill.setDeleteFlag(false);
        bill.setCustomerId(customerId);
        bill.setEvent(event);
        bill.setEventName(event.getName());
        bill.setStatus(receipt.getStatus());
        bill.setArrayItem(receipt.getArrayDataItem());
        billRepo.save(bill);
        if(receipt.getStatus().equals(EnumManager.Billtatus.STAFFCHECK)){
            Optional<CustomerPoint> customerPointOptional = customerPointRepo.findByCustomerAndEvent(customerId,eventId);

            if (customerPointOptional.isEmpty()) {
                CustomerPoint cus  = new CustomerPoint();
                cus.setEvent(event);
                cus.setCustomer(customer);
                cus.setPoint(0);
                customerPointRepo.save(cus);
            }
        }

//            String uploadDir = System.getProperty("user.dir") + storageDirectory +"/";
//            File destination = new File(uploadDir + fileName);
//
//            file.transferTo(destination);

    }

    @Override
    public CustomerEvent findByCustomerIdAndEventId(Integer customerId, Integer eventId) {
        return customerEventRepo.findByCustomerIdAndEventId(customerId,eventId);
    }

    @Override
    public CustomerEvent save(CustomerEvent customerEvent) {
        return customerEventRepo.save(customerEvent);
    }

}