package com.zalo.Spring_Zalo.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ProductDto {
    private int id;
    private String name;
    private boolean status;
    private String picture;
    private int point;
    private LocalDateTime createAt;
    private String publicId;
    private String company;
}
