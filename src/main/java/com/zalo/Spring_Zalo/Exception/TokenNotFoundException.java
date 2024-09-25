package com.zalo.Spring_Zalo.Exception;

import lombok.Data;

@Data
public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException(String resourceName) {
        super(resourceName);

    }
}
