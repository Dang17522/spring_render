package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "customer_point")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPoint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int point ;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_id")
    private Event event;

    @Override
    public String toString() {
        return "CustomerPoint [id=" + id + ", point=" + point + ", customer=" + customer + ", event=" + event + "]";
    }
}
