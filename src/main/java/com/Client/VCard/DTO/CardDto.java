package com.Client.VCard.DTO;

import java.time.LocalDate;

import com.Client.VCard.Entity.CardCategory;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.CardStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
	
	private Integer cardNumber;
    private Integer rewardLimits;
    private Integer balance;
    private LocalDate dateOfCard;
    private Integer pin;
    private CardStatus status;  // Assuming you want to use a string for the enum value
    private Integer associateId;
    private String pinstring;
	private CardCategory cardType;
	
}
