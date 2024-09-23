package com.Client.VCard.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public CardEntity createCard(@RequestBody EmployeeDto employeeDTO) {
		EmployeeEntity employee = employeeDTO.getEmployee();
		if (employee.getAssociateId() == null) {
	        throw new CustomRunTimeException("Associate ID is required");
	    }
		CardEntity card = cardService.createCard(employeeDTO.getAssociateId());
		return card;
	}
	
	@PutMapping("pin")
	public CardEntity setPinNumber(@RequestBody CardDto cardDto ) {
		Integer card = cardDto.getCardNumber();
		if(card == null) {
			throw new CustomRunTimeException("Card Number cannot be null");
		}
		return  employeeService.setPinNumber(cardDto.getCardNumber(), cardDto.getPinstring());
		
		 
		
	}
	
	@PostMapping("vpa")
	public BankDetails setVpa(@RequestBody EmployeeDto employeeDTO) {
		EmployeeEntity employee = employeeDTO.getEmployee();
		if (employee.getAssociateId() == null) {
	        throw new CustomRunTimeException("Associate ID is required");
	    }
		BankDetails bank = employeeService.setVpa(employeeDTO.getAssociateId());
		
		return bank;
		
	}
	
	@PostMapping("convert")
	public MTransaction convert(@RequestBody TransactionDto transactDto) {
		Integer card = transactDto.getCardNumber();
		if(card == null) {
			throw new CustomRunTimeException("Card Number cannot be null");
		}
		
		MTransaction mtr = employeeService.convert(transactDto.getCardNumber(), transactDto.getAmountOfPoints(),transactDto.getPin(), transactDto.getVpa());
		return mtr;
	}
	
	
	
}
