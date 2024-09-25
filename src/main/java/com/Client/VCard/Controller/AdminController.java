package com.Client.VCard.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Client.VCard.DTO.EmployeeDto;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Entity.PerfData;
import com.Client.VCard.Entity.RewardPoints;
import com.Client.VCard.Exception.CustomRunTimeException;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Repository.EmployeeRepository;
import com.Client.VCard.Service.CardService;

@RestController
@RequestMapping(path="/api/user" , headers="Accept=application/json")
public class AdminController {
	
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private CardService cardService;
	@Autowired
	private CardRepository cardRepo;
	

	@PutMapping("approve")
	public CardEntity approveCard(@RequestBody EmployeeDto employeeDTO) {
		EmployeeEntity employee = employeeDTO.getEmployee();
		if (employee == null) {
	        throw new CustomRunTimeException("No Applicationfor this employee");
	    }
		CardEntity card = cardService.approveCard(employee);
		return card;
	}
	
	@PutMapping("reject")
	public CardEntity rejectCard(@RequestBody EmployeeDto employeeDTO) {
		EmployeeEntity employee = employeeDTO.getEmployee();
		if (employee == null) {
	        throw new CustomRunTimeException("No Application for this employee");
	    }
		CardEntity card = cardService.rejectCard(employee);
		return card;
	}
	
	@Scheduled(cron = "0 0 0 1 * ?")
	@PostMapping("send")
	public RewardPoints monthlyRewards(){
		return cardService.monthlyRewards();
		
	}
	
	@PostMapping("mdata")
	public PerfData MData(@RequestParam("file") MultipartFile file) throws Exception {
		return cardService.MData(file);
	}
	
}
