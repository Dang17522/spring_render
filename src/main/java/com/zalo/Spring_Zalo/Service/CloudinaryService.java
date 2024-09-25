package com.zalo.Spring_Zalo.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile  file);

    void deleteImageUpload(String  file);
}
