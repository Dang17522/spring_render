package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "bill_code")
    private String billCode;
    @Column(name = "scan_date")
    private LocalDate scanDate;
     @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "update_date")
    private LocalDateTime updateDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 20)
    private EnumManager.Billtatus status;
    @Column(name = "point", columnDefinition = "int default 0")
    private int point = 0;
    @Column(name = "image")
    private String image;

    private String arrayItem;

    private String infomation;

    @Column(name = "buy_date")
    private String buyDate;

    @Column(name = "product_item")
    private String productItem;
    @Column(name = "delete_flag", columnDefinition = "boolean default false")
    private Boolean deleteFlag;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonManagedReference
    private Event event;

    @Column(name = "Customer")
    private int customerId;
    @Column(name = "eventName")
    private String eventName;
}
