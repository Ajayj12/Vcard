package com.Client.VCard.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardCategoryDto {
	private Integer id;
	
	private String category;
	
	private Double conversionRate;
	
	private Double pointMultiplier;

}
