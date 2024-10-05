package com.Client.VCard.Service;


import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Client.VCard.DTO.BankDetailsDto;
import com.Client.VCard.DTO.CardCategoryDto;
import com.Client.VCard.DTO.CardDto;
import com.Client.VCard.DTO.EmployeeDto;
import com.Client.VCard.DTO.TransactionDto;
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
	@Autowired
	private ModelMapper modelMapper;
	
	
	public CardDto setPinNumber(CardDto cardDto) {
		
		String str = cardDto.getPinstring();
		
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(cardDto.getAssociateId());
		if(empOptional.isEmpty()) {
			throw new CustomIllegalArguementException("No card");
		}
		EmployeeEntity emp = empOptional.get();
		Optional<CardEntity> cardopt = cardRepo.findByCardNumber(cardDto.getCardNumber());
		if(str.length()!= 4 || !str.matches("\\d{4}")) {
			throw new CustomIllegalArguementException("Pin Should be 4 digits");
		}
	
		else if(cardopt.isEmpty()) {
			throw new CustomIllegalArguementException("No card with the Card Number");
		}
		
		CardEntity card = cardopt.get();
		
		LocalDate currentDate = LocalDate.now();
		LocalDate dateOfApproval = card.getDateOfCard();
		
		if(Period.between(currentDate, dateOfApproval).getDays() > 30) {
			card.setStatus(CardStatus.REJECTED);
			
			
			
			modelMapper.map(cardRepo.save(card), CardDto.class);
			
			
			throw new CustomIllegalArguementException("Activation time Expired");
		}
		
		int pin = Integer.parseInt(str);
		 
		if(card.getPin() == pin) {
			throw new CustomIllegalArguementException("Pin cannot be same as previous one");
		}
		card.setPin(pin);
		card.setEmployee(emp);
		
		
		return modelMapper.map(cardRepo.save(card), CardDto.class);
		
		
		
	}
	
	
	
	public BankDetailsDto setVpa(BankDetailsDto bankdetails) {
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(bankdetails.getAssociateId());
	    if (empOptional.isEmpty()) {
	        throw new CustomRunTimeException("Employee not found for associateId: " + bankdetails.getAssociateId());
	    }
	    
	    EmployeeEntity emp = empOptional.get();
	    
	    BankDetails bank = new BankDetails();
	    
	   
	    bank.setVpaId(emp.getMobile());
	    bank.setEmployee(emp);
	    
	
	    return modelMapper.map(bankDetailsRepo.save(bank), BankDetailsDto.class);
	}
	
	
	

	
	public TransactionDto convert(TransactionDto transactDto) {
		Optional<CardEntity> cardopt = cardRepo.findByCardNumber(transactDto.getCardNumber());
		if(cardopt.isEmpty()) {
			throw new CustomIllegalArguementException("No Card with the Card Number");
		}
		Date Cd = new Date();
		
		CardEntity card = cardopt.get();
		MTransaction mtr = new MTransaction();
		
		EmployeeEntity empOptional = card.getEmployee();
	    if (empOptional.getAssociateId() == null) {
	        throw new CustomRunTimeException("associateId cannot be null");
	    }
	    
		
		BankDetails bank = bankDetailsRepo.findByEmployee_AssociateId(empOptional.getAssociateId());

		CardCategory ctype = card.getCardType();
		LocalDate currentDate = LocalDate.now();
		int dayOfMonth = currentDate.getDayOfMonth();
		double finalWithdrawal = (transactDto.getAmountOfPoints() * ctype.getConversionRate());
		
		if(card.getStatus() != CardStatus.ACTIVE){
			throw new CustomIllegalArguementException("Not Eligible");
		}
		
		else if(card.getBalance() <= card.getRewardLimits()/2){
			throw new CustomIllegalArguementException("Balance is less than half of the Limit");
		}
		
		else if(dayOfMonth >= 1 && dayOfMonth <= 25){
			throw new CustomIllegalArguementException("Conversion is Only between 26th and 31st of Month ");
		}
		
		else if (transactDto.getAmountOfPoints() > card.getBalance()) {
            throw new CustomIllegalArguementException("Not enough points to convert.");
        }
		
		else if(card.getPin() != transactDto.getPin()) {
			mtr.setTransactionType(TransactionType.CONVERSION);
			mtr.setStatus(Status.FAILED);
			mtr.setCost(finalWithdrawal);
			mtr.setTransactionDate(Cd);
			mtr.setCard(card);
			
			
			modelMapper.map(transactRepo.save(mtr), TransactionDto.class);
			
			throw new CustomIllegalArguementException("Wrong PIN entered.");

		}
		
		else if(!bank.getVpaId().equals(transactDto.getVpa())) {
			mtr.setTransactionType(TransactionType.CONVERSION);
			mtr.setStatus(Status.FAILED);
			mtr.setCost(finalWithdrawal);
			mtr.setTransactionDate(Cd);
			mtr.setCard(card);
			
			modelMapper.map(transactRepo.save(mtr), TransactionDto.class);
			throw new CustomIllegalArguementException("Wrong UPI entered.");

		}
		
		
	
		
		
		int pts = card.getBalance() - transactDto.getAmountOfPoints();
		
		bank.setVpaId(transactDto.getVpa());
		mtr.setTransactionType(TransactionType.CONVERSION);
		mtr.setStatus(Status.SUCCESS);
		card.setBalance(pts);
		mtr.setCost(finalWithdrawal);
		
		mtr.setTransactionDate(Cd)	;	
		mtr.setCard(card);
		
		return modelMapper.map(transactRepo.save(mtr), TransactionDto.class);
		
		

	}
	
	
	
	
	
	public TransactionDto Buy(TransactionDto transactDto) {
		Optional<CardEntity> cardopt = cardRepo.findByCardNumber(transactDto.getCardNumber());
		if(cardopt.isEmpty()) {
			throw new CustomIllegalArguementException("Wrong Card Number");
		}
		Date Cd = new Date();
		CardEntity card = cardopt.get();
		
		EmployeeEntity emp = card.getEmployee();
		
		MTransaction mtr = new MTransaction();
		
		CardCategory ctype = card.getCardType();
		
		double finalWithdrawal = (transactDto.getAmountOfPoints() * ctype.getConversionRate());
		
		
	    if (emp == null) {
	        throw new CustomRunTimeException("Employee not found");
	    }
	    
	    
	    if(card.getStatus() != CardStatus.ACTIVE){
			throw new CustomIllegalArguementException("Not Eligible");
		}
		
		else if(card.getBalance() <= transactDto.getAmountOfPoints()){
			throw new CustomIllegalArguementException("Insufficient Balance");
		}
	    
		else if(card.getPin() != transactDto.getPin()) {
			mtr.setTransactionType(transactDto.getTransactType());
			mtr.setStatus(Status.FAILED);
			mtr.setCost(finalWithdrawal);
			mtr.setTransactionDate(Cd);
			mtr.setCard(card);
			
			
			
			modelMapper.map(transactRepo.save(mtr), TransactionDto.class);
			
			
			throw new CustomIllegalArguementException("Wrong PIN entered.");

		}
	    
	    int pts = card.getBalance() - transactDto.getAmountOfPoints();
	    
	    mtr.setTransactionType(transactDto.getTransactType());
		mtr.setStatus(Status.SUCCESS);
		card.setBalance(pts);
		mtr.setCost(finalWithdrawal);
		mtr.setTransactionDate(Cd);
		mtr.setCard(card);
		
		
		return modelMapper.map(transactRepo.save(mtr), TransactionDto.class);
	    
		
	}
	
	

	
	
	

}
