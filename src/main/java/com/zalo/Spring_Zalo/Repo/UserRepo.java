package com.zalo.Spring_Zalo.Repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zalo.Spring_Zalo.Entities.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = :username and u.password= :password")
    Optional<User> findByUserNameAndPassword(@Param("username") String username, @Param("password") String password);
    
    @Query("SELECT u FROM User u WHERE u.username = :username ")
    User findByUserName(String username);

    @Query("SELECT COUNT(u) FROM User u JOIN u.company c WHERE c.name = :companyName")
    long countByBusinessName(@Param("companyName") String companyName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role")
    Page<User> findAllWithRole(Pageable pageable);

}
