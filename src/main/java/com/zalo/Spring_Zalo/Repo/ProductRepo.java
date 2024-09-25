package com.zalo.Spring_Zalo.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.zalo.Spring_Zalo.Entities.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Integer>{
    @Query("select p from Product p where p.company.id = :companyId order by p.createAt desc ")
    Page<Product> findAllbyCompanyId(@Param("companyId") Integer companyId, Pageable pageable);

    @Query("select p from Product p  order by p.createAt desc ")
    Page<Product> findAllOrDerBOrderByCreateAt( Pageable pageable);
    @Query(value = "SELECT * FROM products WHERE company_id =:companyId AND id NOT IN (SELECT product_id FROM product_events WHERE company_id =:companyId AND event_id =:eventId)", nativeQuery = true)
    List<Product> findAllByNotInProductEvents(@Param("companyId") Integer companyId, @Param("eventId") Integer eventId);

    @Query(value = "SELECT * FROM products WHERE concat(product_name) LIKE %:key% AND company_id =:companyId AND id NOT IN (SELECT product_id FROM product_events WHERE company_id =:companyId AND event_id =:eventId)", nativeQuery = true)
    List<Product> searchByNotInProductEvents(@Param("key") String key,@Param("companyId") Integer companyId, @Param("eventId") Integer eventId);
}
