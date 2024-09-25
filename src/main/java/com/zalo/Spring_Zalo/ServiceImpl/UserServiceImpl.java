package com.zalo.Spring_Zalo.ServiceImpl;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Roles;
import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.JWT.CustomUserDetailsService;
import com.zalo.Spring_Zalo.JWT.JWTGenerator;
import com.zalo.Spring_Zalo.Password_User_Web.PasswordUtils;
import com.zalo.Spring_Zalo.Repo.UserRepo;
import com.zalo.Spring_Zalo.Response.AdminLoginResponse;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Response.LoginResponse;
import com.zalo.Spring_Zalo.Response.UserInfoResponse;
import com.zalo.Spring_Zalo.Service.UserService;
import com.zalo.Spring_Zalo.request.UserRequestLogin;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    
    @Autowired
    public UserServiceImpl(UserRepo userRepo, CustomUserDetailsService customUserDetailsService, JWTGenerator jwtGenerator) {
        this.userRepo = userRepo;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtGenerator = jwtGenerator;
    }

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    private final JWTGenerator jwtGenerator;

    @Override
    public ResponseEntity<?> AuthorizeUser(UserRequestLogin user) {
        User userAuthorize = userRepo.findByUserName(user.getUsername());
        System.out.println(">>userAuthorize" + userAuthorize);
        if (userAuthorize!= null) {
            System.out.println(">>check login here");
            User authenticatedUser = userAuthorize;
             
//            if (authenticatedUser.getStatus().equals(EnumManager.UserStatus.WAITING_FOR_APPROVAL) || authenticatedUser.getStatus().equals(EnumManager.UserStatus.DECLINED)) {
//                return ResponseEntity.badRequest().body(new AdminLoginResponse("Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt", false, 401, 0));
//            }
            // checking password 
            String passwordCheck = PasswordUtils.decryptPassword(authenticatedUser.getPassword());
            System.out.println(passwordCheck);
            if (!passwordCheck.equals(user.getPassword())) {
                return ResponseEntity.badRequest().body(new AdminLoginResponse("Sai thông tin tài khoản hoặc mật khẩu ! ", false, 401, 0));
            }
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticatedUser.getUsername());
           
            String accesstoken = jwtGenerator.generateAccessToken(userDetails,authenticatedUser);
            String refeshtoken = jwtGenerator.generateRefreshToken(userDetails,authenticatedUser);
            System.out.println(">> Access : " + accesstoken);
            System.out.println(">> Refesh : " + refeshtoken);
            UserInfoResponse userReady = new UserInfoResponse(authenticatedUser.getId(), authenticatedUser.getUsername(), authenticatedUser.getEmail(),authenticatedUser
            .getAvatar(),
             authenticatedUser.getStatus(), authenticatedUser.isIs_active(),authenticatedUser.getRole().getRoleName(),authenticatedUser.getCompany().getId(),authenticatedUser.getFullname(),authenticatedUser.getCompany().getName());
            System.out.println(">>userReady" + userReady);
            return ResponseEntity.ok(new LoginResponse("Đăng nhập thành công", true, 200,userReady, accesstoken, refeshtoken));
        }
        return ResponseEntity.badRequest().body(new AdminLoginResponse("Tên đăng nhập hoặc mật khẩu không đúng", false, 401, 0));
    }

    @Override
    public User authenticateUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticateUser'");
    }
}
