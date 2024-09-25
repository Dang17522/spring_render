package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 30, name = "reward_name")
    private String name;
    @Column(name = "point_reward")
    private int pointReward;
    @Column(name = "quantity_reward")
    private int quantity;
    private String image;
    private String publicId;
    private int reward_type;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<CustomerReward> customerRewards = new ArrayList<>();

}
