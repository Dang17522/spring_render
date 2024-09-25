package com.zalo.Spring_Zalo.Service;

import org.springframework.http.ResponseEntity;

import com.zalo.Spring_Zalo.DTO.DataStatistiken;

public interface DataService {
 ResponseEntity <DataStatistiken> Datenstatistiken();

}
