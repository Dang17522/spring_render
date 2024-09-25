package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepo extends JpaRepository<Company, Integer> {
    @Query("SELECT c FROM Company c WHERE c.name = :companyName")
    Company findByCompanyName(@Param("companyName") String companyName);


}
