package com.Client.VCard.Service;


import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Client.VCard.Entity.BankDetails;
import com.Client.VCard.Entity.CardCategory;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.CardStatus;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Entity.Limitpoints;
import com.Client.VCard.Entity.MTransaction;
import com.Client.VCard.Entity.Purchases;
import com.Client.VCard.Entity.Status;
import com.Client.VCard.Entity.TransactionType;
import com.Client.VCard.Exception.CustomIllegalArguementException;
import com.Client.VCard.Exception.CustomRunTimeException;
import com.Client.VCard.Repository.BankDetailsRepository;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Repository.EmployeeRepository;
import com.Client.VCard.Repository.TransactionsRepository;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private CardRepository cardRepo;
	@Autowired
	private BankDetailsRepository bankDetailsRepo;
	
	@Autowired
	private TransactionsRepository transactRepo;
	
	
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
	
	
	
	public BankDetails setVpa(Integer associateId) {
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(associateId);
	    if (!empOptional.isPresent()) {
	        throw new CustomRunTimeException("Employee not found for associateId: " + associateId);
	    }
	    EmployeeEntity emp = empOptional.get();
	    
	    BankDetails bank = new BankDetails();
	    
	    bank.setEmployee(emp);
	    bank.setVpaId(emp.getMobile());
	    
	    bankDetailsRepo.save(bank);
	    
	    return bank;
	}
	
	
	

	
	public MTransaction convert(Integer cardNumber, int amountOfPoints, int pin, String vpa) {
		Optional<CardEntity> cardopt = cardRepo.findByCardNumber(cardNumber);
		if(cardopt.isEmpty()) {
			throw new CustomIllegalArguementException("Wrong Card Number");
		}
		Date Cd = new Date();
		CardEntity card = cardopt.get();
		MTransaction mtr = new MTransaction();
		EmployeeEntity empOptional = card.getEmployee();
	    if (empOptional.getAssociateId() == null) {
	        throw new CustomRunTimeException("associateId not found");
	    }
	    int associateId = empOptional.getAssociateId();
		
		BankDetails bank = bankDetailsRepo.findByEmployee_AssociateId(associateId);

		CardCategory ctype = card.getCardType();
		LocalDate currentDate = LocalDate.now();
		int dayOfMonth = currentDate.getDayOfMonth();
		double finalWithdrawal = (amountOfPoints * ctype.getConversionRate());
		
		if(card.getStatus() != CardStatus.ACTIVE){
			throw new CustomIllegalArguementException("Not Eligible");
		}
		
		else if(card.getBalance() <= card.getRewardLimits()/2){
			throw new CustomIllegalArguementException("Balance is less than half of the Limit");
		}
		
		else if(dayOfMonth >= 1 && dayOfMonth <= 23){
			throw new CustomIllegalArguementException("Conversion is Only between 26th and 31st of Month ");
		}
		
		else if (amountOfPoints > card.getBalance()) {
            throw new CustomIllegalArguementException("Not enough points to convert.");
        }
		
		else if(card.getPin() != pin) {
			mtr.setTransactionType(TransactionType.CONVERSION);
			mtr.setStatus(Status.FAILED);
			mtr.setCost(finalWithdrawal);
			mtr.setTransactionDate(Cd);
			mtr.setCard(card);
			transactRepo.save(mtr);
			throw new CustomIllegalArguementException("Wrong PIN entered.");

		}
		
		else if(!bank.getVpaId().equals(vpa)) {
			mtr.setTransactionType(TransactionType.CONVERSION);
			mtr.setStatus(Status.FAILED);
			mtr.setCost(finalWithdrawal);
			mtr.setTransactionDate(Cd);
			mtr.setCard(card);
			transactRepo.save(mtr);
			throw new CustomIllegalArguementException("Wrong UPI entered.");

		}
		
		
	
		
		
		int pts = card.getBalance() - amountOfPoints;
		
		bank.setVpaId(vpa);
		mtr.setTransactionType(TransactionType.CONVERSION);
		mtr.setStatus(Status.SUCCESS);
		card.setBalance(pts);
		mtr.setCost(finalWithdrawal);
		
		mtr.setTransactionDate(Cd)	;	
		mtr.setCard(card);
		
		return transactRepo.save(mtr);
		
		

	}
	
	
	

}
