package com.zalo.Spring_Zalo.Service;

import org.springframework.http.ResponseEntity;

import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.request.UserRequestLogin;

public interface UserService {

    ResponseEntity<?> AuthorizeUser(UserRequestLogin user);
    User authenticateUser(String username, String password);
}
