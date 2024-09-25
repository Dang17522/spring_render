package com.zalo.Spring_Zalo.Response;

import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.request.UserRequestLogin;

public class LoginResponse  extends ApiResponse {
    private String Token;
    private String RefeshToken;
    private UserInfoResponse user ;
    public UserInfoResponse getUser() {
        return user;
    }
    public void setUser(UserInfoResponse user) {
        this.user = user;
    }
    public String getToken() {
        return Token;
    }
    public void setToken(String token) {
        Token = token;
    }
    public String getRefeshToken() {
        return RefeshToken;
    }
    public void setRefeshToken(String refeshToken) {
        RefeshToken = refeshToken;
    }
    @Override
    public String toString() {
        return "LoginResponse [Token=" + Token + ", RefeshToke=" + RefeshToken + "]";
    }

    public LoginResponse(String message, boolean success, int status, UserInfoResponse user, String accessToken, String refreshToken) {
        super(message, success, status);
        this.user = user;
        this.Token = accessToken;
        this.RefeshToken = refreshToken;
    }
   
    
    
    
}
