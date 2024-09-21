package com.Client.VCard.Entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "V_transaction")
public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String transactionType;
	
	private String status;
	
	private Double cost;
	
	private Date transactionDate;
	
	@ManyToOne
	@JoinColumn(name = "card_id")
	private CardEntity card;
	
	
	@ManyToOne
	@JoinColumn(name = "purchase_id")
	private Purchases purchase;
	
	

}
