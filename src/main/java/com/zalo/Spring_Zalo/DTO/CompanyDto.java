package com.zalo.Spring_Zalo.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyDto {
    private String name;
    private String nameManager;
    private String email;
    private String phoneNumber;
    private String address;
}
