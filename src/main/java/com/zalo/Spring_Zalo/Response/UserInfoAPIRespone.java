package com.zalo.Spring_Zalo.Response;

import com.zalo.Spring_Zalo.Entities.User;

public class UserInfoAPIRespone extends ApiResponse {
    private User user ;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserInfoAPIRespone(String string, boolean b, int i, User user) {
        super(string, b, i, user);
    }

   
    
}
