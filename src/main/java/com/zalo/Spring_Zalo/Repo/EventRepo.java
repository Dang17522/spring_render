package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Integer> {
    @Query("select e from Event e where e.company.id =:companyId")
    Page<Event> findAllbyCompanyId(@Param("companyId") Integer companyId,Pageable pageable);
}
