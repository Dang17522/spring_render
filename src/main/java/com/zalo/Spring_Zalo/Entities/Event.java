package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zalo.Spring_Zalo.DTO.UserConstants;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "event_name", nullable = false, length = 40)
    private String name;
    @Column(name = "Banner",nullable = false, length = 255)
    private String banner;
    @Column(name="description",nullable = true , length = 255)
    private String description;     
    @Column(name = "time_start_event")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate timeStartEvent;
    @Column(name = "time_end_event")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate timeEndEvent;
    @Column(name="IsVisible", nullable = false)
    private boolean isVisible = UserConstants.isVisible;

    @Column(name = "public_id", length = 30)
    private String publicId;



    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ProductEvent> productEvents = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
            @JsonIgnore
    List<Bill> bills = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    List<Reward> rewards = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<CustomerEvent> customerEvents = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<CustomerReward> customerRewards = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<CustomerPoint> customerPoints = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonManagedReference
    private Company company;
    @Override
public String toString() {
    return "Event [id=" + id + ", name=" + name + ", banner=" + banner + ", description=" + description
            + ", timeStartEvent=" + timeStartEvent + ", timeEndEvent=" + timeEndEvent + ", isVisible=" + isVisible
            + ", company=" + (company != null ? company.getName() : "null") + "]";
}
    

    

     
}
