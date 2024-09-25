package com.zalo.Spring_Zalo.Controller;

import java.util.List;
import java.util.Map;

import com.zalo.Spring_Zalo.DTO.UserDto;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.Roles;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Repo.RolesRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.Repo.UserRepo;
import com.zalo.Spring_Zalo.Response.UserInfoAPIRespone;
import com.zalo.Spring_Zalo.Response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin/accounts")
public class AccountController {
    @Autowired
    private  UserRepo userRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private RolesRepo roleRepo;

    @Autowired
    private CloudinaryService cloudinaryService;
    private final ObjectMapper mapper = new ObjectMapper();

    // @GetMapping("/")
    // public ResponseEntity<List<User>> getListAccount() {
    //     List<User> list = userRepo.findAll();
    //     if(list.isEmpty() ){
    //         throw new ResponseStatusException(404, "No account found", null);
    //     }
    //     return ResponseEntity.ok(list);
    // }
 @GetMapping("/")
    public ResponseEntity<?> getAllAccount(@RequestParam(defaultValue = "5") int pageSize,
                                                    @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> usersWithRole = userRepo.findAllWithRole(pageable);
        Page<UserDto> map = usersWithRole.map(this::mapToDto);
        return ResponseEntity.ok(map);
    }
        
    @PutMapping("/update-account")
    public ResponseEntity<?> postMethodName(@RequestPart("id") String id,
                                            @RequestPart("fullname") String fullName,
                                            @RequestPart("email") String email,
                                            @RequestPart("companyId") String companyId,
                                            @RequestPart("roleName") String roleName,
                                            @RequestPart("roleId") String roleId,
                                            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            System.out.println("id: " + id + ", fullName: " + fullName + ", email: " + email + ", companyId: " + companyId + ", roleName: " + roleName + ", roleId: " + roleId);
            User existingUser = userRepo.findById(Integer.parseInt(id)).orElseThrow(() -> new ResourceNotFoundException("user", "id", Integer.parseInt(id)));
            Company company = companyRepo.findById(Integer.parseInt(companyId)).orElseThrow(() -> new ResourceNotFoundException("company", "id", Integer.parseInt(companyId)));
            Roles role = roleRepo.findById(Integer.parseInt(roleId)).orElseThrow(() -> new ResourceNotFoundException("role", "id", Integer.parseInt(roleId)));
            existingUser.setFullname(fullName);
            existingUser.setEmail(email);
            existingUser.setCompany(company);
            if(!role.getRoleName().equals(roleName)){
                role.setRoleName(EnumManager.UserRole.fromValue(roleName));
                roleRepo.save(role);
            }
            if(file != null){
                if(existingUser.getPublicId() != null){
                    cloudinaryService.deleteImageUpload(existingUser.getPublicId());
                }
                Map data = cloudinaryService.upload(file);
                existingUser.setAvatar(String.valueOf(data.get("secure_url")));
                existingUser.setPublicId(String.valueOf(data.get("public_id")));
            }
            userRepo.save(existingUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body", e);
        }
        ApiResponse apiResponse = new ApiResponse("Update account success !!!", true, 200);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
    private UserDto mapToDto(User user){
        return  new UserDto().builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .is_active(user.isIs_active())
                .status(user.getStatus())
                .roleName(user.getRole().getRoleName())
                .company(user.getCompany().getName())
                .companyId(user.getCompany().getId())
                .roleId(user.getRole().getId())
                .build();
    }
}
