package com.zalo.Spring_Zalo.request;



import lombok.*;

import java.io.Serializable;

@Getter
@Setter
public class UserRequestSignUp {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private int companyId;
}
