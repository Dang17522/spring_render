package com.zalo.Spring_Zalo.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.zalo.Spring_Zalo.request.UserTestData;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserTestData user) {
        logger.info("Received registration request for username: {}", user.getUsername());
       
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
