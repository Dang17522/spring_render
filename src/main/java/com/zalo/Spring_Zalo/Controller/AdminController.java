package com.zalo.Spring_Zalo.Controller;

import java.util.HashMap;
import java.util.Optional;

import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zalo.Spring_Zalo.DTO.DataStatistiken;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Roles;
import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.JWT.JWTGenerator;
import com.zalo.Spring_Zalo.Password_User_Web.PasswordUtils;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Repo.RolesRepo;
import com.zalo.Spring_Zalo.Repo.UserRepo;
import com.zalo.Spring_Zalo.Response.AdminLoginResponse;
import com.zalo.Spring_Zalo.Response.AdminLoginToken;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.UserService;
import com.zalo.Spring_Zalo.ServiceImpl.DataServiceImpl;
import com.zalo.Spring_Zalo.request.RequestChangeStatusAccountBody;
import com.zalo.Spring_Zalo.request.UserRequestLogin;
import com.zalo.Spring_Zalo.request.UserRequestSignUp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired 
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RolesRepo roleRepo;
    @Autowired
    private CompanyRepo companyRepo;
       @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private DataServiceImpl DataService;
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody UserRequestLogin user) {
        return userService.AuthorizeUser(user);
      

    }


    @PostMapping("/register")
    public ApiResponse RegisterAccount (@RequestBody UserRequestSignUp entity) {
        if(entity == null ){
            return new ApiResponse("Đăng kí thất bại do thiếu thông tin", false,403);
        }
       
        if( userRepo.findByUserName(entity.getUsername())!= null){
            return new ApiResponse("Đăng kí thất bại do đã tồn tại tài khoản ", false,409);
        }
        Company companyInfo = companyRepo.findById(entity.getCompanyId()).orElseThrow(() -> new ResourceNotFoundException("company","companyId",entity.getCompanyId()));
        String userRoles ="User";
        Roles roles = new Roles();
        if(companyInfo.getId() == 1){
            userRoles = "Admin";
            roles.setRoleName(EnumManager.UserRole.Admin);
        }else{
            roles.setRoleName(EnumManager.UserRole.User);
        }
        
        User user = new User();
        String encryptedPassword = PasswordUtils.encryptPassword(entity.getPassword()); // move to function encrypt password
        user.setUsername(entity.getUsername());
        user.setPassword(encryptedPassword);
        user.setEmail(entity.getEmail());
        user.setFullname(entity.getUsername());
        user.setCompany(companyInfo);

        roles.setUser(user);
        roleRepo.save(roles);

        user.setRole(roles);
        user.setStatus(EnumManager.UserStatus.WAITING_FOR_APPROVAL);
        user.setIs_active(false);
        user.setAvatar("https://i.pinimg.com/736x/0c/ce/24/0cce244c8456e9632233b1921450f5af.jpg");//default the avatar
        userRepo.save(user);        
    return new ApiResponse("Đăng kí thành công", true, 200);
    }

    @PostMapping("/updateUserData")
    public ResponseEntity<User> updateUserData(@RequestBody User updatedUserData) {
    //   System.out.println(updatedUserData.getFullname());
      if (updatedUserData.getId() == 0) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    // Tìm kiếm người dùng trong cơ sở dữ liệu
    User user = userRepo.findByUserName(updatedUserData.getUsername());
    if (user != null) {
        user.setFullname(updatedUserData.getFullname());
        // Lưu người dùng được cập nhật
        userRepo.save(user);
        // System.out.println(">>after: "+ user );
        return new ResponseEntity<>(user, HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    }
    @GetMapping("/dataStatistiken")
    public ResponseEntity<DataStatistiken> getDataStatistiken() {
         // getData() trả về đối tượng DataStatistiken
        return DataService.Datenstatistiken();
    }
} 
