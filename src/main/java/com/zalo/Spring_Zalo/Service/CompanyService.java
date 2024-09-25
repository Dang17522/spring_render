package com.zalo.Spring_Zalo.Service;

import com.zalo.Spring_Zalo.DTO.CompanyDto;
import com.zalo.Spring_Zalo.Entities.Company;

public interface CompanyService {
    Company creeateCompany(Company company);
    Company updateCompany(CompanyDto company, Integer companyId);
    Company getCompanyById(Integer companyId);
}
