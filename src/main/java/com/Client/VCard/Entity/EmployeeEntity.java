package com.Client.VCard.Entity;

import java.time.LocalDate;
import java.util.Date;

import org.antlr.v4.runtime.misc.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
@Table(name = "V_employee")
public class EmployeeEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	
	@Column(unique = true)
	private  Integer associateId;
	
	@Enumerated(EnumType.STRING)
	private Position position;
	private String emailId;
	private String mobile;
	private LocalDate dateOfHire;
	
	
	
	@JsonIgnore
	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
	private CardEntity card;

}
