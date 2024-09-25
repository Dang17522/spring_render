package com.zalo.Spring_Zalo.Entities;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemPoint {
    
    private String name;
    
    private int quantity;

    private Optional<Integer> point;


    


    





    public ItemPoint(String name, int quantity, Optional<Integer> point) {
        this.name = name;
        this.quantity = quantity;
        this.point = point;
    }











    @Override
    public String toString() {
        return "Item [name=" + name + ", quantity=" + quantity + ", point=" + point + "]";
    }





    





    
}

