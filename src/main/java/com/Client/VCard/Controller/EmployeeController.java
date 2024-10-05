package com.Client.VCard.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Client.VCard.DTO.BankDetailsDto;
import com.Client.VCard.DTO.CardDto;
import com.Client.VCard.DTO.EmployeeDto;
import com.Client.VCard.DTO.TransactionDto;
import com.Client.VCard.Entity.BankDetails;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Entity.MTransaction;
import com.Client.VCard.Exception.CustomRunTimeException;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Service.CardService;
import com.Client.VCard.Service.EmployeeService;

@RestController
@RequestMapping(path="/api/user" , headers="Accept=application/json")
public class EmployeeController {
	@Autowired
	private CardService cardService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CardRepository cardRepo;
	
	
	@PostMapping("newcard")
	public CardDto createCard(@RequestBody EmployeeDto employee) {
		
		if (employee == null) {
	        throw new CustomRunTimeException("Associate ID is required");
	    }
		CardDto card = cardService.createCard(employee);
		return card;
	}
	
	@PutMapping("pin")
	public CardDto setPinNumber(@RequestBody CardDto cardDto) {
		Integer card = cardDto.getCardNumber();
		if(card == null) {
			throw new CustomRunTimeException("Card Number cannot be null");
		}
		return  employeeService.setPinNumber(cardDto);
		
		 
		
	}
	
	@PostMapping("vpa")
	public BankDetailsDto setVpa(@RequestBody BankDetailsDto bankDTO) {
		Integer emp = bankDTO.getAssociateId();
		if (emp == null) {
	        throw new CustomRunTimeException("Associate ID is required");
	    }
		BankDetailsDto bank = employeeService.setVpa(bankDTO);
		
		return bank;
		
	}
	
	@PostMapping("convert")
	public TransactionDto convert(@RequestBody TransactionDto transactDto) {
		Integer card = transactDto.getCardNumber();
		if(card == null) {
			throw new CustomRunTimeException("Card Number cannot be null");
		}
		
		TransactionDto mtr = employeeService.convert(transactDto);
		return mtr;
	}
	
	
	@PostMapping("Buy")
	public TransactionDto Buy(@RequestBody TransactionDto transactDto) {
		Integer card = transactDto.getCardNumber();
		if(card == null) {
			throw new CustomRunTimeException("Card Number cannot be null");
		}
		
		TransactionDto mtr = employeeService.Buy(transactDto);
		return mtr;
	}
	
	
}
