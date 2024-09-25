package com.zalo.Spring_Zalo.Controller;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import org.springframework.http.*;
@RestController
public class OcrSpace {

    private static final String OCR_SPACE_API_KEY = "K84948378488957";
    private static final String OCR_API_URL = "https://api.ocr.space/parse/image";

    @PostMapping("/scanText")
    public String scanText(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Chuyển đổi hình ảnh thành dạng Base64
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Tạo request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo request body với các tham số cần thiết
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("apiKey", OCR_SPACE_API_KEY);
        body.add("base64Image", base64Image);

        // Tạo HTTP entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Thực hiện cuộc gọi API OCR.space
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                OCR_API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Lấy kết quả từ response
        String ocrApiResponse = responseEntity.getBody();

        // Xử lý kết quả và trả về
        return processOcrApiResponse(ocrApiResponse);
    }

    private String processOcrApiResponse(String ocrApiResponse) {
        // Xử lý định dạng kết quả từ API OCR.space theo định dạng bạn mong muốn
        // Đây chỉ là một ví dụ, bạn có thể cần phải phân tích JSON hoặc XML kết quả để lấy thông tin cần thiết.
        return ocrApiResponse;
    }
}
