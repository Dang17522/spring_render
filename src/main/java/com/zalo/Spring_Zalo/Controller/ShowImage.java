package com.zalo.Spring_Zalo.Controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")

public class ShowImage {
    @Value("${Storge_Directory}")
    private String storageDirectory;

    @GetMapping("/show/{imageName}")
    public ResponseEntity<Resource> getMethodName(@PathVariable  String imageName) throws IOException{
       Path imagePath = Paths.get(storageDirectory).resolve(imageName);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() || imageResource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    
}
