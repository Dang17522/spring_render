package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.CustomerPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerPointRepo extends JpaRepository<CustomerPoint,Integer> {
    @Query("select c from CustomerPoint  c where c.customer.id =:customerId and c.event.id =:eventId")
    Optional<CustomerPoint> findByCustomerAndEvent(@Param("customerId")Integer customerId, @Param("eventId") Integer eventId);

    void saveAndFlush(Optional<CustomerPoint> customerPoint);
}
