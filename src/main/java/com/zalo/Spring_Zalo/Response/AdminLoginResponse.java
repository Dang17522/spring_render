package com.zalo.Spring_Zalo.Response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse extends ApiResponse {
    private int role_id; // Change to role_id for role code

    public AdminLoginResponse(String message, boolean success, int status, int role_id) {
        super(message, success, status);
        this.role_id = role_id;
    }
}
