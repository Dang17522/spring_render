package com.zalo.Spring_Zalo.Entities;

import lombok.Data;


@Data
public class MiddlewareAndCustomer {
    private Middleware middleware;
    private Customer customer;
    @Override
    public String toString() {
        return "MiddlewareAndCustomer [middleware=" + middleware + ", customer=" + customer + "]";
    }
}

