package com.Client.VCard.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.CardStatus;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Exception.CustomIllegalArguementException;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private CardRepository cardRepo;
	
	
	
	public CardEntity setPinNumber(Integer cardNumber, String str) {
		
		
		
		Optional<CardEntity> cardopt = cardRepo.findByCardNumber(cardNumber);
		if(str.length()!= 4 || !str.matches("\\d{4}")) {
			throw new CustomIllegalArguementException("Pin Should be 4 digits");
		}
		

		
		else if(cardopt.isEmpty()) {
			throw new CustomIllegalArguementException("Wrong Card Number");
		}
		
		CardEntity card = cardopt.get();
		
		LocalDate currentDate = LocalDate.now();
		LocalDate dateOfApproval = card.getDateOfCard();
		
		if(Period.between(currentDate, dateOfApproval).getDays() > 30) {
			card.setStatus(CardStatus.REJECTED);
			cardRepo.save(card);
			
			throw new CustomIllegalArguementException("Activation time Expired");
		}
		
		int pin = Integer.parseInt(str);
		 
		if(card.getPin() == pin) {
			throw new CustomIllegalArguementException("Pin cannot be same as previous one");
		}
		card.setPin(pin);
		
		return cardRepo.save(card);
		
		
		
	}
	
	
	

}
