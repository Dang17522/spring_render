package com.zalo.Spring_Zalo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangeStatusAccountBody {
    private int userId;
    private Boolean is_Active;
    @Override
    public String toString() {
        return "RequestChangeStatusAccountBody [userId=" + userId + ", is_Active=" + is_Active + "]";
    }
}
