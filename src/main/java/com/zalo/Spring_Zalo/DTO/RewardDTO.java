package com.zalo.Spring_Zalo.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RewardDTO {
    
    private int id;
    private String name;
    private int pointReward;
    private int quantity;
    private String image;
    private int reward_Type;
    private int eventId;
    private String eventName;

    
}
