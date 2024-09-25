package com.zalo.Spring_Zalo.Response;
import com.zalo.Spring_Zalo.Entities.EnumManager;
public class UserInfoResponse {

    

        private int id;
        private String username;
        private String email;
        private String avatar;
        private EnumManager.UserStatus status;
        private boolean active;
        private EnumManager.UserRole role;
        private String bussinessName;

        private int companyId;
        private String fullname;
        // Other constructors, getters, and setters as needed

        
    
        // Constructors, getters, and setters
    
        public UserInfoResponse(int id, String username, String email,String avatar, EnumManager.UserStatus status, boolean active,EnumManager.UserRole role,int companyId,String fullname,String bussinessName) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.avatar = avatar;
            this.status = status;
            this.active = active;
            this.role = role;
            this.companyId = companyId;
            this.fullname = fullname;
            this.bussinessName = bussinessName;
        }
    
        // Other constructors, getters, and setters as needed
    
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public EnumManager.UserStatus getStatus() {
            return status;
        }

        public void setStatus(EnumManager.UserStatus status) {
            this.status = status;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public EnumManager.UserRole getRole() {
            return role;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setRole(EnumManager.UserRole role) {
            this.role = role;
        }

        public String getFullname() {
            return fullname;
        }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    @Override
        public String toString() {
            return "UserInfoResponse [id=" + id + ", username=" + username + ", fullname=" + fullname + ", email=" + email + ", avatar=" + avatar
                    + ", status=" + status + ", active=" + active + ", role=" + role + ",companyId=" + companyId+ " + \",BussinessName=\" + bussinessName + \"]";
        }

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int bussinessName) {
            this.companyId = bussinessName;
        }

       

        
    
    



}
