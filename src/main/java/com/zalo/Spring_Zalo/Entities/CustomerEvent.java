package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "create_date")

    private LocalDateTime createDate;
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
