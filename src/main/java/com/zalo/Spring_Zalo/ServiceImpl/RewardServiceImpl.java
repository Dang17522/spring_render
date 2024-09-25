package com.zalo.Spring_Zalo.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zalo.Spring_Zalo.Entities.Reward;
import com.zalo.Spring_Zalo.Repo.RewardRepo;
import com.zalo.Spring_Zalo.Service.RewardService;
@Service
public class RewardServiceImpl implements RewardService{ 
    private RewardRepo rewardRepo;
    @Autowired
    public RewardServiceImpl(RewardRepo rewardRepo) {
        this.rewardRepo = rewardRepo;
    }
    @Override
    public Reward addReward(Reward reward) {
        Reward Newreward = rewardRepo.saveAndFlush(reward);
        return Newreward;
    }
}