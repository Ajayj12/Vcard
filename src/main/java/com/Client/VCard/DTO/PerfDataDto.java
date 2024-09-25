package com.Client.VCard.DTO;

import com.Client.VCard.Entity.Performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfDataDto {
	private Integer performance;
    private Integer daysWorked;
    private Integer associateId;
}
