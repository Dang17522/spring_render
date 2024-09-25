package com.zalo.Spring_Zalo.Exception;

import lombok.Data;

@Data
public class ApiNotFoundException extends RuntimeException{
    public ApiNotFoundException(String resourceName) {
        super(resourceName);

    }
}
