package com.zalo.Spring_Zalo.Repo;

import com.zalo.Spring_Zalo.Entities.CustomerReward;
import com.zalo.Spring_Zalo.Entities.Reward;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
@Repository
public interface RewardRepo extends JpaRepository<Reward,Integer> {
     @Query("SELECT r FROM Reward r WHERE r.event.id = :eventId")
    Page<Reward> findByEvent_Id(@Param("eventId") Integer eventId, Pageable pageable);

    @Query("SELECT r FROM Reward r WHERE r.event.company.id =: companyId order by r.createAt desc ")
    Page<Reward> findByCompany_Id(@Param("eventId") Integer companyId, Pageable pageable);

    @Query("SELECT r FROM Reward r order by r.createAt desc")
    Page<Reward> findALlPage(Pageable pageable);

    @Query("SELECT r from Reward r where r.event.id = :eventId AND r.id = :rewardId")
    Reward getInfoReward(@Param("eventId") Integer eventId,@Param("rewardId") Integer rewardId);


}
