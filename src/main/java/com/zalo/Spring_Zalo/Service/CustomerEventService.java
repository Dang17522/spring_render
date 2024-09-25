package com.zalo.Spring_Zalo.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zalo.Spring_Zalo.Entities.CustomerEvent;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Receipt;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.zalo.Spring_Zalo.Entities.Line;
import com.zalo.Spring_Zalo.Response.ReplacementResult;

public interface CustomerEventService {
    Object ScanResult(String result, Locale currentLocale, Integer eventId,Integer customerId);
    Object ScanResultOCR(String result, Locale currentLocale, Integer eventId,Integer customerId);
    Object ScanResultOCRMapping(List<Line> linesList, Locale currentLocale,Integer eventId,Integer customerId);
    Object ProcessingLine(List<Line> lines , Locale currentLocale,Integer customerId, Integer eventId);
    ReplacementResult keyexchange(String text, Locale currentLocale);
    ResponseEntity<String> processFile(MultipartFile file, String json, Locale currentLocale, Integer customerId, Integer eventId);

    void saveBillImage(MultipartFile file, int eventId, int customerId, Receipt receipt);


    CustomerEvent findByCustomerIdAndEventId(Integer customerId, Integer eventId);

    CustomerEvent save(CustomerEvent customerEvent);

}
