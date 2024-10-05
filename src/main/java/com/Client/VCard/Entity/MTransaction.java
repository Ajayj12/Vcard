package com.Client.VCard.Entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "V_Mtransaction")
public class MTransaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private Double cost;
	
	private Date transactionDate;
	
	@ManyToOne
	@JoinColumn(name = "card_id" , referencedColumnName = "cardNumber" )
	private CardEntity card;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "purchase_id")
	private Purchases purchase;
	
	

}
