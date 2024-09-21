package com.Client.VCard.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.EmployeeEntity;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer>{

	CardEntity findByEmployee_AssociateId(Integer associateId);

	boolean existsByEmployee(EmployeeEntity emp);

	Optional<CardEntity> findByCardNumber(Integer cardNumber);
	
	

}
