package com.zalo.Spring_Zalo.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.zalo.Spring_Zalo.DTO.DataStatistiken;
import com.zalo.Spring_Zalo.Repo.CustomerRepo;
import com.zalo.Spring_Zalo.Repo.EventRepo;
import com.zalo.Spring_Zalo.Service.DataService;
@Service
public class DataServiceImpl implements DataService{
    @Autowired
    private EventRepo eventRepo; 
    @Autowired
    private CustomerRepo customerRepo; 
    @Override 
    public ResponseEntity <DataStatistiken> Datenstatistiken(){
        DataStatistiken dataStatistiken = new DataStatistiken();
        dataStatistiken.setEventCounts(eventRepo.count());
        dataStatistiken.setTotalPoints(0);
        dataStatistiken.setTotalCustomers(customerRepo.count());
        dataStatistiken.setYourBalance(0);
        return ResponseEntity.ok(dataStatistiken);
    }
}
