package com.zalo.Spring_Zalo.ServiceImpl;

import com.zalo.Spring_Zalo.Entities.Customer;
import com.zalo.Spring_Zalo.Entities.CustomerReward;
import com.zalo.Spring_Zalo.Entities.Helper;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CustomerRepo;
import com.zalo.Spring_Zalo.Service.CustomerService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepo customerRepo;


    @Override
    public Customer createAccounCustomer(Customer customer) {
        Customer saveCustomer = customerRepo.saveAndFlush(customer);
        return saveCustomer;
    }

    @Override
    public Customer getUserById(Integer userId) {
        Customer customer = customerRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("customer","customerId", userId));
        return customer;
    }

    @Override
    public Customer getUserByZaloId(String zaloId) {
        Customer customer = customerRepo.getByIdZalo(zaloId) ;
        return customer;
    }

    @Override
    public Customer findUserByZaloId(String zaloId) {
        return customerRepo.findByIdZalo(zaloId);
    }

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Customer updateCustomer(Customer customer, Integer  userId) {
        if(userId == null) {
            createAccounCustomer(customer);
        }
        Customer cus = customerRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("customer","customerId", userId));
        cus.setPhone(customer.getPhone());
        Customer updateCustomer = customerRepo.saveAndFlush(cus);
        return updateCustomer;
    }

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Customer updateAddressCustomer(Customer customer, Integer userId) {
        Customer cus = customerRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("customer","customerId", userId));
        cus.setAddress(customer.getAddress());
        Customer updateCustomer = customerRepo.saveAndFlush(cus);
        return updateCustomer;
    }

    @Override
    public ByteArrayInputStream getExportDataExcel() throws IOException {
        List<Object[]> list = customerRepo.findAllDataObject();
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcelByCustomer(list);
        return byteArrayInputStream;
    }

    @Override
    public ByteArrayInputStream getExportDataExcelByCompanyId(Integer companyId) throws IOException {
        List<Object[]> list = customerRepo.findAllDataObjectByCompanyId(companyId);
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcelByCustomer(list);
        return byteArrayInputStream;
    }


}
