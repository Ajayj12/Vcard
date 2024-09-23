package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Integer>{

	
	BankDetails findByEmployee_AssociateId(int associateId);

}
