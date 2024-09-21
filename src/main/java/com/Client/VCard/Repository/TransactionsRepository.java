package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.Transaction;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Integer> {

}
