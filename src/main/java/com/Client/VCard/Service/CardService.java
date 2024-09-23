package com.Client.VCard.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.CardStatus;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Entity.Limitpoints;
import com.Client.VCard.Entity.Position;
import com.Client.VCard.Exception.CustomIllegalArguementException;
import com.Client.VCard.Exception.CustomRunTimeException;
import com.Client.VCard.Repository.CardCategoryRepository;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Repository.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
public class CardService {

	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private CardRepository cardRepo;
	@Autowired
	private CardCategoryRepository cardCategoryRepo;
	
	

	@Transactional
	public CardEntity createCard(Integer associateId) {

		
		
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(associateId);
	    if (!empOptional.isPresent()) {
	        throw new CustomRunTimeException("Employee not found for associateId: " + associateId);
	    }
	    EmployeeEntity emp = empOptional.get();
	    
	    if (cardRepo.existsByEmployee(emp)) {
	        throw new CustomRunTimeException("Card already applied for associateId: " + associateId);
	    }

		CardEntity card = new CardEntity();
		card.setEmployee(emp);
		card.setCardNumber(emp.getAssociateId());

		if (emp.getPosition() == Position.G1) {
			card.setRewardLimits(Limitpoints.GOLD.getPts());
			card.setBalance(Limitpoints.GOLD.getPts());
			card.setCardType(cardCategoryRepo.getById(1));
		} 
		else if (emp.getPosition() == Position.G3) {
			card.setRewardLimits(Limitpoints.DIAMOND.getPts());
			card.setBalance(Limitpoints.DIAMOND.getPts());
			card.setCardType(cardCategoryRepo.getById(2));
		} 
		else if (emp.getPosition() == Position.G4) {
			card.setRewardLimits(Limitpoints.PLATINUM.getPts());
			card.setBalance(Limitpoints.PLATINUM.getPts());
			card.setCardType(cardCategoryRepo.getById(3));
		}else {
			throw new CustomIllegalArguementException("You're not Eligible");
		}

		card.setDateOfCard(LocalDate.now());
		card.setPin(1000);
		card.setStatus(CardStatus.PENDING);

		return cardRepo.save(card);

	}

	public CardEntity approveCard(EmployeeEntity employee) {
		if (employee == null) {
			throw new CustomIllegalArguementException("Employee cannot be null.");
		}

		CardEntity card = cardRepo.findByEmployee_AssociateId(employee.getAssociateId());
		LocalDate currentDate = LocalDate.now();
		
		LocalDate doH = employee.getDateOfHire();
		
		if (card == null) {
			throw new CustomIllegalArguementException("Invalid card.");
		}
		else if (card.getStatus() == CardStatus.REJECTED) {
			throw new CustomIllegalArguementException("Card is Rejected.");
		}
		else if (card.getStatus() != CardStatus.PENDING) {
			throw new CustomIllegalArguementException("Card is already approved.");
		}
		

		else if (doH != null && Period.between(doH, currentDate).getYears() < 1) {
			card.setStatus(CardStatus.REJECTED);
			cardRepo.save(card);
			throw new CustomIllegalArguementException(
					"Card cannot be approved since employment under company is less than 1 year.");
	
		}
		
		card.setStatus(CardStatus.ACTIVE);

		return cardRepo.save(card);

	}

	public CardEntity rejectCard(EmployeeEntity employee) {
		if (employee == null) {
			throw new IllegalArgumentException("Employee cannot be null.");
		}

		CardEntity card = cardRepo.findByEmployee_AssociateId(employee.getAssociateId());
		LocalDate currentDate = LocalDate.now();
		LocalDate doH = employee.getDateOfHire();
		
		 if (card.getStatus() == CardStatus.REJECTED) {
				throw new CustomIllegalArguementException("Card is already Rejected.");
			}
		
		 else if(card.getStatus() == CardStatus.ACTIVE) {
			card.setStatus(CardStatus.PENDING);
			cardRepo.save(card);
			throw new CustomIllegalArguementException("Card is Blocked Successfully.");
			
		}

		else if (card.getStatus() != CardStatus.PENDING && card.getStatus() != CardStatus.REJECTED) {
			throw new CustomIllegalArguementException("Card is already approved.");
		}


		card.setStatus(CardStatus.REJECTED);

		return cardRepo.save(card);
		
		

	}

}
