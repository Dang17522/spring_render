package com.zalo.Spring_Zalo.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPointReturn {
    private List<ItemPoint> items;
    private Integer totalPoint;

}
