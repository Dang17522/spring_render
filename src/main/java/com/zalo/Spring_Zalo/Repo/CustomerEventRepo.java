package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.Customer;
import com.zalo.Spring_Zalo.Entities.CustomerEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerEventRepo extends JpaRepository<CustomerEvent, Integer> {
    @Query("SELECT c FROM CustomerEvent c where c.customer.id =:customerId and c.event.id =:eventId")
    CustomerEvent findByCustomerIdAndEventId(@Param("customerId") Integer customerId,@Param("eventId") Integer eventId);

    @Query("SELECT c.customer FROM CustomerEvent c WHERE c.event.company.id =:companyId")
    Page<Customer> findAllCustomerByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);
    
}
