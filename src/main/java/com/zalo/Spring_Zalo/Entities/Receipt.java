package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Receipt {
    private String receiptId;

    private String infomation;
    private String buyDate;
    private List<ItemReceipt> listItems;
    private List<ItemPoint> listDataItems;
    private EnumManager.Billtatus status = EnumManager.Billtatus.STAFFCHECK;
    private Integer totalPoint = 0;

    private String arrayDataItem;
}
