package com.Client.VCard.Entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.Client.VCard.Exception.CustomIllegalArguementException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "V_card")
public class CardEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer cardNumber;
	private Integer rewardLimits;
	
	private Integer balance;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "card_type_id")
	private CardCategory cardType;
	private LocalDate dateOfCard;
	
	
	
	private Integer pin;
	
	@Enumerated(EnumType.STRING)
	private CardStatus status;
	
	@ManyToOne
	@JoinColumn(name = "associate_id" ,referencedColumnName = "associateId")
	private EmployeeEntity employee;
	
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "card" , cascade = CascadeType.ALL )
	private List<MTransaction> transactions;
	
	
	public void setPin(int pin) {
	    if (pin < 1000 || pin > 9999) {
	        throw new CustomIllegalArguementException("PIN must be exactly 4 digits.");
	    }
	    this.pin = pin;
	}
	
	

}
