package com.zalo.Spring_Zalo.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zalo.Spring_Zalo.Entities.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BillRepo  extends JpaRepository<Bill,Integer> {

    Bill findByBillCode(String billCode);

    @Query("select b from Bill b where b.event.company.id =: companyId")
    Page<Bill> findByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);

    @Query("select b from Bill b where b.event.company.id =:companyId and b.event.id =:eventId")
    Page<Bill> findByCompanyIdAndEventId(@Param("companyId") Integer companyId,@Param("eventId") Integer eventId, Pageable pageable);

    @Query("select b from Bill b where b.event.id =:eventId")
    Page<Bill> findByEventId(@Param("eventId") Integer eventId, Pageable pageable);

}
