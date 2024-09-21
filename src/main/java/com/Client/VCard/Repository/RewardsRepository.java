package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.RewardPoints;

@Repository
public interface RewardsRepository extends JpaRepository<RewardPoints, Integer> {

}
