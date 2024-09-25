package com.zalo.Spring_Zalo.request;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
public class BillRequest {

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
}