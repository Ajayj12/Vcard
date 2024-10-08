package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.DTO.PerfDataDto;
import com.Client.VCard.Entity.PerfData;

@Repository
public interface PerformanceRepository extends JpaRepository<PerfData, Integer>  {

	PerfDataDto findByEmployee_AssociateId(Integer associateId);

}
