package com.Client.VCard.DTO;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RewardPointsDto {
	
	private Integer monthlyawards;
	private Date awardedDate;
	private Integer cardNumber;

}
