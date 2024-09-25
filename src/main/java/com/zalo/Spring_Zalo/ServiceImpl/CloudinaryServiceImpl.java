package com.zalo.Spring_Zalo.ServiceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Override
    public Map upload(MultipartFile file) {
        Map params = ObjectUtils.asMap(
                "folder", "zalo"
        );
        try {
            Map data =  cloudinary.uploader().upload(file.getBytes(),params);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Image uploading fail !!");
        }
    }

    @Override
    public void deleteImageUpload(String file) {
        try {
            cloudinary.api().deleteResources(Arrays.asList(file), ObjectUtils.asMap("type", "upload", "resource_type", "image"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
