package com.zalo.Spring_Zalo.ServiceImpl;

import com.zalo.Spring_Zalo.DTO.CompanyDto;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Service.CompanyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepo repo;


    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Company creeateCompany(Company company) {
        return repo.saveAndFlush(company);
    }

    @Override
    @Transactional(rollbackOn = { Throwable.class })
    public Company updateCompany(CompanyDto company, Integer companyId) {
        Company c = repo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company","companyId",companyId));
        c.setName(company.getName());
        c.setPhoneNumber(company.getPhoneNumber());
        c.setEmail(company.getEmail());
        c.setAddress(company.getAddress());
        c.setNameManager(company.getNameManager());
        System.out.println(company.getNameManager());
        Company updateCompany = repo.saveAndFlush(c);
        return updateCompany;
    }

    @Override
    public Company getCompanyById(Integer companyId) {
        Company c =  repo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("company","companyId",companyId));
        return c;
    }
}
