package com.zalo.Spring_Zalo.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zalo.Spring_Zalo.Entities.Bill;
import com.zalo.Spring_Zalo.Entities.Receipt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

public interface BillService {

    Bill findBillByCode(String billCode);
    Receipt jsonScanReceipt(String jsonResponse, Locale locale, Integer eventId) throws JsonProcessingException;
    void processFileScan(Receipt receipt, MultipartFile file, Integer customerId, Integer eventId);
}
