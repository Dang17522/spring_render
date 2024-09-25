package com.zalo.Spring_Zalo.DTO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Event;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
public class BillDto {
    private int id;
    private String billCode;
    private LocalDate scanDate;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private EnumManager.Billtatus status;
    private int point = 0;
    private String image;
    private Boolean deleteFlag;
    private int eventId;
    private int customerId;
    private String eventName;
    private String zaloId;
    private String phone;
    private String arrayItem;
    private String company;
    private String infomation;
    private String buyDate;
}
