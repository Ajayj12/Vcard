package com.Client.VCard.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Client.VCard.Entity.MTransaction;


@Repository
public interface TransactionsRepository extends JpaRepository<MTransaction, Integer> {

}
