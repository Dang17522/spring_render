package com.zalo.Spring_Zalo.Exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fileName;
    Integer fileValue;

    public ResourceNotFoundException(String resourceName, String fileName,Integer fileValue) {
        super(String.format("%s not found with  %s : %s ", resourceName, fileName,fileValue));
        this.resourceName = resourceName;
        this.fileName= fileName;
        this.fileValue = fileValue;
    }
}
