package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Customer getByIdZalo(String zaloId);
    Customer findByIdZalo(String zaloId);

    @Query(value = "SELECT customers.id,customers.customer_name,customers.phone,customers.add_ress,events.event_name, customer_point.point FROM customers left join  customer_events on customers.id = customer_events.customer_id left join customer_point on customer_events.customer_id = customer_point.customer_id left join events on customer_events.event_id = events.id group by phone", nativeQuery = true)
    List<Object[]> findAllDataObject();


    @Query(value = "SELECT customers.id,customers.customer_name,customers.phone,customers.add_ress,events.event_name, customer_point.point FROM customers left join  customer_events on customers.id = customer_events.customer_id left join customer_point on customer_events.customer_id = customer_point.customer_id left join events on customer_events.event_id = events.id where events.company_id = ?1;", nativeQuery = true)
    List<Object[]> findAllDataObjectByCompanyId(Integer companyId);
}
