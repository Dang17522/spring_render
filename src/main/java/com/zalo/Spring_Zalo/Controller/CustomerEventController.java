package com.zalo.Spring_Zalo.Controller;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;


import com.zalo.Spring_Zalo.Entities.*;
import com.zalo.Spring_Zalo.Entities.Event;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CustomerRepo;
import com.zalo.Spring_Zalo.Repo.ProductEventRepo;
import com.zalo.Spring_Zalo.Service.BillService;
import com.zalo.Spring_Zalo.Service.CustomerEventService;
import com.zalo.Spring_Zalo.Service.CustomerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.Spring_Zalo.Repo.BillRepo;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.request.FileStorageManager;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/customer_event")
public class CustomerEventController {

    @Autowired
    private CustomerEventService customerEventService;

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private BillService billService;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ProductEventRepo productEventRepo;

    private final String storageDirectory = FileStorageManager.getStorageDirectory();

    private static final Logger logger = LoggerFactory.getLogger(CustomerEventController.class);
    @PostMapping("/event/{eventId}/customer/{customerId}/upload")
    public Object  handleFileUpload(@RequestPart("file") MultipartFile[] files,@PathVariable("customerId") Integer customerId, @PathVariable("eventId")Integer eventId) {
        List<Receipt> list = new ArrayList<>();
        Receipt receipt = new Receipt();
        try {
            for (MultipartFile file: files){
                String originalFilename = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                // Convert MultipartFile to BufferedImage
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                System.out.println("bufferedImage: " + bufferedImage);
                logBufferedImageSize(bufferedImage);
                BufferedImage resizeImage = resizeImage(bufferedImage, bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);
                System.out.println("resizeImage: " + resizeImage);
                logBufferedImageSize(resizeImage);
                // Create a temporary PNG file
                File tempFile = File.createTempFile("temp", "." + originalFilename);

                // Use try-with-resources to ensure the file is closed
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    // Write BufferedImage to PNG file
                    ImageIO.write(resizeImage, originalFilename, os);
                }
                LocaleContextHolder.setLocale(Locale.US);
                Locale currentLocale = LocaleContextHolder.getLocale();
                // Use try-with-resources to ensure the HttpClient is closed
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    // Use try-with-resources to ensure the response entity is closed
                    try (CloseableHttpResponse response = httpClient.execute(createOcrSpaceRequest(tempFile))) {
                        HttpEntity responseEntity = response.getEntity();
                        if (responseEntity != null) {
                            // Read the JSON response from OCR Space API
                            String jsonResponse = EntityUtils.toString(responseEntity);
                            receipt = billService.jsonScanReceipt(jsonResponse, currentLocale,eventId);
                            System.out.println("jsonResponse: " + jsonResponse);
                            if (receipt.getStatus().equals(EnumManager.Billtatus.ERROR)) {
                                ApiResponse apiResponse = new ApiResponse("Error processing the image", false, 400);
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
                            }else if (receipt.getStatus().equals(EnumManager.Billtatus.NOCONTENT)) {
                                customerEventService.saveBillImage(file,eventId, customerId, receipt);
//                                list.add(receipt);
                                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Bill no content");
                            }else if(receipt.getStatus().equals(EnumManager.Billtatus.DISABLE)){
                                ApiResponse apiResponse = new ApiResponse("This bill already Used!", false, 409);
                                return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
//                                list.add(receipt);
                            } else if (receipt.getStatus().equals(EnumManager.Billtatus.STAFFCHECK) || receipt.getReceiptId() == null || receipt.getReceiptId().equals("")) {
                                customerEventService.saveBillImage(file,eventId, customerId, receipt);
                                ApiResponse apiResponse = new ApiResponse("Staff Check", true, 201);
//                                list.add(receipt);
                                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
                            }else {
                                billService.processFileScan(receipt ,file, customerId ,eventId);
//                                list.add(receipt);
                            }
                        }
                    }
                }
            }
        }catch (ClientProtocolException e) {
            System.out.println("cach1");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        } catch (IOException e) {
            System.out.println("cach2");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing the image.");
        }catch (Exception e) {
            System.out.println("cach3");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        }
        ItemPointReturn itemPointReturn = new ItemPointReturn();
        itemPointReturn.setTotalPoint(receipt.getTotalPoint());
        itemPointReturn.setItems(receipt.getListDataItems());
        return new ResponseEntity<>(itemPointReturn,HttpStatus.OK);
        }

    @PostMapping("/scan-bill")
    public String  test(@RequestPart("file") MultipartFile file) {
        try {

             // Convert MultipartFile to BufferedImage
             BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

             // Create a temporary PNG file
             File tempFile = File.createTempFile("temp", ".png");
 
             // Write BufferedImage to PNG file
             ImageIO.write(bufferedImage, "png", tempFile);
 
             // Use OCR Space API to process the image
             String apiKey = "K86621687288957"; // Replace with your OCR Space API key
             String ocrSpaceApiUrl = "https://api.ocr.space/parse/image";
 
             CloseableHttpClient httpClient = HttpClients.createDefault();
             HttpPost httpPost = new HttpPost(ocrSpaceApiUrl);
 
             MultipartEntityBuilder builder = MultipartEntityBuilder.create();
             builder.addTextBody("apikey", apiKey);
             builder.addTextBody("OCREngine", "2");
             builder.addTextBody("language", "eng");
             builder.addTextBody("isOverlayRequired", "true");
 
             builder.addBinaryBody("file", tempFile, ContentType.MULTIPART_FORM_DATA, tempFile.getName());
 
             HttpEntity multipart = builder.build();
             httpPost.setEntity(multipart);
 
             HttpResponse response = httpClient.execute(httpPost);
             HttpEntity responseEntity = response.getEntity();
 
             if (responseEntity != null) {
                 String result = EntityUtils.toString(responseEntity);
                 String parsedText = extractParsedTextOB(result);
                 Locale currentLocale = LocaleContextHolder.getLocale();
                 // return service.ScanResultOCR(parsedText, currentLocale, eventId,customerId);
                 return parsedText;
             }
             httpClient.close();
 
         } catch (ClientProtocolException e) {
             e.printStackTrace();
             return "Error processing the image. Client Protocol Exception.";
         } catch (IOException e) {
             e.printStackTrace();
             return "Error processing the image. IO Exception.";
         }
 
         return "Error processing the image. An unexpected exception occurred.";
    }
 
    private String extractParsedTextOB(String ocrResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(ocrResponse);

            JsonNode parsedResults = jsonNode.path("ParsedResults");

            if (parsedResults.isArray() && parsedResults.size() > 0) {
                JsonNode firstResult = parsedResults.get(0);
                JsonNode parsedText = firstResult.path("ParsedText");

                if (parsedText.isTextual()) {
                    return parsedText.asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception as needed
        }

        return null; // Return null if ParsedText is not found or an error occurs
    }
    


    /**
     * 
     * @param file
     * @return
     */
   @PostMapping("/event/{eventId}/customer/{customerId}/scan-bill-OCR")
    public Object testOCRSpace(@RequestPart("file") MultipartFile file,@PathVariable("customerId") Integer customerId, @PathVariable("eventId")Integer eventId) {
        try {
            // int eventId= 3;
            // int customerId =1;
            // Convert MultipartFile to BufferedImage
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

            // Create a temporary PNG file
            File tempFile = File.createTempFile("temp", ".png");

            // Write BufferedImage to PNG file
            ImageIO.write(bufferedImage, "png", tempFile);

            // Use OCR Space API to process the image
            String apiKey = "K86621687288957"; // Replace with your OCR Space API key
            String ocrSpaceApiUrl = "https://api.ocr.space/parse/image";

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(ocrSpaceApiUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("apikey", apiKey);
            builder.addTextBody("OCREngine", "2");
            builder.addTextBody("language", "eng");
            builder.addTextBody("isOverlayRequired", "true");

            builder.addBinaryBody("file", tempFile, ContentType.MULTIPART_FORM_DATA, tempFile.getName());

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity);
                String parsedText = extractParsedText(result);
                Locale currentLocale = LocaleContextHolder.getLocale();
                return customerEventService.ScanResultOCR(parsedText, currentLocale, eventId,customerId);
                // return parsedText;
            }

            httpClient.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
    }
/**
 * Thís ís the function take out String parsedText from OCR result , return String with long text to handle 
 * @param ocrResponse
 * @return
 */
    private String extractParsedText(String ocrResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(ocrResponse);
    
            JsonNode parsedResults = jsonNode.path("ParsedResults");
            //handle Array of Object 
            if (parsedResults.isArray() && parsedResults.size() > 0) {
                JsonNode firstResult = parsedResults.get(0);
                JsonNode parsedText = firstResult.path("ParsedText");
    
                return parsedText.isTextual() ? parsedText.asText() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log or handle the exception as needed
        }
    
        return null; // Return null if ParsedText is not found or an error occurs
    }
    
    /**
     * 
     * @param file
     * @return
     */
   @PostMapping("/event/{eventId}/customer/{customerId}/scan-bill-OCR-mapping")
   public Object testOCRSpaceMapping(@RequestPart("file") MultipartFile file,@PathVariable("customerId") Integer customerId, @PathVariable("eventId")Integer eventId) {
        try {
            // Convert MultipartFile to BufferedImage
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

            // Create a temporary PNG file
            File tempFile = File.createTempFile("temp", ".png");

            // Use try-with-resources to ensure the file is closed
            try (OutputStream os = new FileOutputStream(tempFile)) {
                // Write BufferedImage to PNG file
                ImageIO.write(bufferedImage, "png", os);
            }
            Locale currentLocale = LocaleContextHolder.getLocale();
            // Use try-with-resources to ensure the HttpClient is closed
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // Use try-with-resources to ensure the response entity is closed
                try (CloseableHttpResponse response = httpClient.execute(createOcrSpaceRequest(tempFile))) {
                    HttpEntity responseEntity = response.getEntity();

                    if (responseEntity != null) {
                        // Read the JSON response from OCR Space API
                        String jsonResponse = EntityUtils.toString(responseEntity);
                        JSONObject jsonResult = new JSONObject(jsonResponse);
                
                        // Get the ParsedResults array
                        JSONArray parsedResults = jsonResult.getJSONArray("ParsedResults");
                
                        if (parsedResults.length() > 0) {
                            // Get the first item from the ParsedResults array
                            JSONObject firstResult = parsedResults.getJSONObject(0);
                
                            // Check if it contains the TextOverlay object
                            if (firstResult.has("TextOverlay")) {
                                // Get the TextOverlay object
                                JSONObject textOverlay = firstResult.getJSONObject("TextOverlay");
                
                                // Check if it contains the Lines array
                                if (textOverlay.has("Lines")) {
                                    // Get the Lines array
                                    JSONArray linesArray = textOverlay.getJSONArray("Lines");
                                    // logger.info(">> Line Array :"+linesArray.length());
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<Line> linesList = new ArrayList<>(Arrays.asList(objectMapper.readValue(linesArray.toString(), Line [].class)));
                                    try {
                                        customerEventService.ProcessingLine(linesList, currentLocale, customerId, eventId);
                                    }catch (Exception e) {
                                        e.printStackTrace();                                        
                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
                                    }
                                    for (int i = 0; i < linesArray.length();i++) {
                                        // Get the Line JSON object
                                        JSONObject lineObject = linesArray.getJSONObject(i);
                                        // Map Line object
                                        Line line = Line.lineFromJson(lineObject);
                                        
                                        // Map Words array inside the Line
                                        if (lineObject.has("Words")) {
                                            JSONArray wordsArray = lineObject.getJSONArray("Words");
                                            Word[] words = Word.wordFromJson(wordsArray);
                                            line.setWords(Arrays.asList(words));
                                            // Add the Line object to the list
                                            linesList.add(line);
                                        }
                                    }
                            
                                    // logger.info(">>lineList: " + linesList);
                                   
                                //  return linesList;
                                return customerEventService.ProcessingLine(linesList, currentLocale, customerId, eventId);
                                    // return service.ScanResultOCRMapping(linesList,currentLocale, customerId, eventId);                   
                            }
                        }
                    }
                }
            }
        }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing the image.");
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the image.");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your image didn't have what we need");
        }
    


    private HttpPost createOcrSpaceRequest(File tempFile) {
        ResourceBundle infomation =  ResourceBundle.getBundle("application");
        String apiKey = infomation.getString("OCRKey"); // Replace with your OCR Space API key
        String ocrSpaceApiUrl = infomation.getString("OcrURL");
        String ocrEngine = infomation.getString("OCREngine");
        String ocrLanguage = infomation.getString("OCRlanguage");
        HttpPost httpPost = new HttpPost(ocrSpaceApiUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody("apikey", apiKey);
        builder.addTextBody("OCREngine",ocrEngine );
        builder.addTextBody("language", ocrLanguage);
//        builder.addTextBody("isOverlayRequired", "true");
        builder.addTextBody("isTable", "true");
        builder.addBinaryBody("file", tempFile, ContentType.MULTIPART_FORM_DATA, tempFile.getName());

        httpPost.setEntity(builder.build());
        return httpPost;
   }
    private String convertEnumToString(int i,Receipt receipt) {
        String text ="";
        if(receipt.getStatus() == EnumManager.Billtatus.APPROVE){
            text = "\n bill " + i + " scan successfully, your get is: " + receipt.getTotalPoint();
        }else if(receipt.getStatus() == EnumManager.Billtatus.STAFFCHECK){
            text = "\n bill " + i + " bill Staffcheck";
        }else if(receipt.getStatus() == EnumManager.Billtatus.DISABLE){
            text = "\n bill " + i + " this bill already Used! ";
        }else {
            text = "\n Bill " + i +  " no content !";
        }
        return text;
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable("customerId") Integer customerId, @PathVariable("eventId") Integer eventId) {
       CustomerEvent customerEvent = customerEventService.findByCustomerIdAndEventId(customerId, eventId);
       if(customerEvent == null) {
           Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
           Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
           CustomerEvent cus = new CustomerEvent();
           cus.setCreateDate(LocalDateTime.now());
           cus.setUpdateDate(LocalDateTime.now());
           cus.setCustomer(customer);
           cus.setEvent(event);
           CustomerEvent cusCreate = customerEventService.save(cus);
           return new ResponseEntity<>(cusCreate, HttpStatus.CREATED);
       }
       customerEvent.setUpdateDate(LocalDateTime.now());
       CustomerEvent cusUpdateTime = customerEventService.save(customerEvent);
       return new ResponseEntity<>(cusUpdateTime, HttpStatus.OK);
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB );
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public void logBufferedImageSize(BufferedImage image) {
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            long sizeInBytes = ((long) width * (long) height * 4L); // Giả sử 4 byte cho mỗi pixel (RGBA)
            long sizeInKB = sizeInBytes / 1024;
            long sizeInMB = sizeInKB / 1024;

            System.out.println("Approximate size: " + sizeInBytes + " bytes");
            System.out.println("Approximate size: " + sizeInKB + " KB");
            System.out.println("Approximate size: " + sizeInMB + " MB");
        } else {
            System.out.println("BufferedImage is null");
        }
    }

}
   

