package com.zalo.Spring_Zalo.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zalo.Spring_Zalo.Entities.Middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/zaloToken")
public class ZaloApiRequest {
        @PostMapping("/")
        public StringBuilder decodeZalo(@RequestBody Middleware mid){
        String accessToken = mid.getAccessToken();
        String apiUrl = "https://graph.zalo.me/v2.0/me/info";
        String code =mid.getPhoneToken(); 
        String appSecretKey = "QQeEC7y7G0DJKdtXlRF9";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("access_token", accessToken);
            connection.setRequestProperty("code",code);
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
            connection.disconnect();
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;  
    }
}