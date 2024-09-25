package com.zalo.Spring_Zalo.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class itWorkController {
    @GetMapping("/work")
    public String getResponse() {
        System.out.println("ping success");
        return "It works!";
    }
}
