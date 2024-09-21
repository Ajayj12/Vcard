package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.Purchases;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchases, Integer> {

}
