package com.zalo.Spring_Zalo.request;

import jakarta.persistence.*;
@Entity
public class UserTestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    // Các trường khác nếu cần

    // Constructors, getters, setters và các phương thức khác

    public UserTestData() {
        // Một constructor mặc định cần thiết cho JPA
    }

    public UserTestData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter và setter cho các trường

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Các phương thức khác nếu cần
}
