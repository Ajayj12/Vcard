package com.Client.VCard.DTO;

import com.Client.VCard.Entity.EmployeeEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {
	
	 	private Integer associateId;
	    private String position;
	    private EmployeeEntity employee;
	
	

}
