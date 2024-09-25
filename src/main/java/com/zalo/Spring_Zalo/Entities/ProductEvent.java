package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id")
    private Event event;
   

    public ProductEvent(Event event, Product product, int point) {
        this.event = event;
        this.product = product;
    }
}
