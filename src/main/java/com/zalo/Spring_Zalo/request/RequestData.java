package com.zalo.Spring_Zalo.request;

import com.zalo.Spring_Zalo.Entities.Customer;
import com.zalo.Spring_Zalo.Entities.Middleware;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestData {
    private Middleware middleware;
    private Customer customer;
    @Override
    public String toString() {
        return "MiddlewareAndCustomer [middleware=" + middleware + ", customer=" + customer + "]";
    }
}
