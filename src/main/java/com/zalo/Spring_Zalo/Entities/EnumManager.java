package com.zalo.Spring_Zalo.Entities;
public class EnumManager{
    public enum UserRole {
        Admin,
        User,
       Staff,
       Partner;
       public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        // Handle unknown roles
        return User;
    }
    }
    public enum UserStatus {
        APPROVED,
        DECLINED,
        WAITING_FOR_APPROVAL
    }
    public enum Billtatus {
        APPROVE,
        STAFFCHECK,
        DISABLE,
        ERROR,

        NOCONTENT
    }
}
