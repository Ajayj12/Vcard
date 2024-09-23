package com.Client.VCard.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
public enum Limitpoints {
	GOLD(4000),
	DIAMOND(6000),
	PLATINUM(8000);
	
	private final int pts;
	 
	 
	
   

}
