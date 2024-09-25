package com.zalo.Spring_Zalo.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Middleware {
    private String accessToken;
    private String phoneToken;

    @Override
    public String toString() {
        return "Middleware [accessToken=" + accessToken + ", phoneToken=" + phoneToken + "]";
    }
}
