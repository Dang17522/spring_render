package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "product_name", nullable = false, length = 30, unique = true)
    private String name;
    private boolean status;
    private String picture;
    private int point;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "public_id", length = 30)
    private String publicId;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<ProductEvent> productEvents = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "company_id")
    private Company company;

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", status=" + status + ", picture=" + picture + ", point="
                + point + ", productEvents=" + productEvents + "]";
    }

    

}
