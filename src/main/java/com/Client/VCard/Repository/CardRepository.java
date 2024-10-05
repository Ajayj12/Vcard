package com.Client.VCard.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.DTO.CardDto;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.EmployeeEntity;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer>{

	Optional<CardEntity> findByEmployee_AssociateId(Integer associateId);

	Optional<CardEntity> findByCardNumber(Integer cardNumber);

	boolean existsByEmployee(EmployeeEntity emp);

	
	

}
