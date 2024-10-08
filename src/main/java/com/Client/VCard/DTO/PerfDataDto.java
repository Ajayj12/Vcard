package com.Client.VCard.DTO;

import com.Client.VCard.Entity.Performance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerfDataDto {
	private Performance performance;
    private Integer daysWorked;
    private Integer associateId;
}
