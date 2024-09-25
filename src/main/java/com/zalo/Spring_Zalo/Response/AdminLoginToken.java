package com.zalo.Spring_Zalo.Response;


public class AdminLoginToken extends ApiResponse {
    private String token;

    public AdminLoginToken(String message, boolean success, int status, String token) {
        super(message, success, status);
        this.token = token;
    }

    // Getters and setters for token
}
