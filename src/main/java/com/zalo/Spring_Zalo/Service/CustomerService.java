package com.zalo.Spring_Zalo.Service;

import com.zalo.Spring_Zalo.Entities.Customer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public interface CustomerService {
    Customer createAccounCustomer(Customer customer);
    Customer getUserById(Integer userId);
    Customer getUserByZaloId(String zaloId);
    Customer findUserByZaloId(String zaloId);
    Customer updateCustomer(Customer customer, Integer userId);
    Customer updateAddressCustomer(Customer customer, Integer userId);

    ByteArrayInputStream getExportDataExcel() throws IOException;

    ByteArrayInputStream getExportDataExcelByCompanyId(Integer companyId) throws IOException;
}
