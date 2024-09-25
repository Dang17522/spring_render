package com.zalo.Spring_Zalo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {
    private int id;
    private String username;
    private String fullname;
    private String email;
    private String avatar = UserConstants.DEFAULT_AVATAR_URL;
    private String password;
    private String company;
    private int companyId;
    private boolean is_active;
    private EnumManager.UserStatus status;
    private EnumManager.UserRole roleName;
    private int roleId;
}
