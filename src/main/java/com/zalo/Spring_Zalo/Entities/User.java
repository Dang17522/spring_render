package com.zalo.Spring_Zalo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zalo.Spring_Zalo.DTO.UserConstants;
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "username", length = 30)
    private String username;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "avatar", nullable = false, length = 100)
    private String avatar = UserConstants.DEFAULT_AVATAR_URL;

    @Column(name = "public_id", length = 100)
    private String publicId;
    
    @Column
    private String password;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id") 
    @JsonIgnore 
    private Company company;
    @Column(name = "is_active")
    private boolean is_active;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 20)
    private EnumManager.UserStatus status;
    
    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonIgnore
    private Roles role;
    // Other fields and relationships


    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
    public String getRoleName() {
        return role != null ? role.getRoleName().name() : null;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", fullname=" + fullname+ ", email=" + email + ", avatar=" + avatar + ", password="
                + password + ", company=" + company + ", is_active=" + is_active + ", status=" + status + ", role="
                + role + "]";
    }
    
}
