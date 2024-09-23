package com.Client.VCard.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDto {
	private Integer cardNumber;
	private Integer amountOfPoints;
	private Integer pin;
	private String vpa;
}
