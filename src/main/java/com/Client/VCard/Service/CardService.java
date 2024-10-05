package com.Client.VCard.Service;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Client.VCard.DTO.CardDto;
import com.Client.VCard.DTO.EmployeeDto;
import com.Client.VCard.DTO.PerfDataDto;
import com.Client.VCard.DTO.RewardPointsDto;
import com.Client.VCard.Entity.CardEntity;
import com.Client.VCard.Entity.CardStatus;
import com.Client.VCard.Entity.EmployeeEntity;
import com.Client.VCard.Entity.Limitpoints;
import com.Client.VCard.Entity.PerfData;
import com.Client.VCard.Entity.Performance;
import com.Client.VCard.Entity.Position;
import com.Client.VCard.Entity.RewardPoints;
import com.Client.VCard.Exception.CustomIllegalArguementException;
import com.Client.VCard.Exception.CustomRunTimeException;
import com.Client.VCard.Repository.CardCategoryRepository;
import com.Client.VCard.Repository.CardRepository;
import com.Client.VCard.Repository.EmployeeRepository;
import com.Client.VCard.Repository.PerformanceRepository;
import com.Client.VCard.Repository.RewardsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import jakarta.transaction.Transactional;

@Service
public class CardService {

	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private CardRepository cardRepo;
	@Autowired
	private CardCategoryRepository cardCategoryRepo;

	@Autowired
	private PerformanceRepository perfRepo;

	@Autowired
	private RewardsRepository rewardPointsRepo;
	@Autowired
	private ModelMapper modelMapper;
	
	

	@Transactional
	public CardDto createCard(EmployeeDto employee) {

		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(employee.getAssociateId());
		if (empOptional.isEmpty()) {
			throw new CustomRunTimeException("Employee not found for associateId: " + employee.getAssociateId());
		}
		
		EmployeeEntity emp = empOptional.get();
		
		if (cardRepo.existsByEmployee(emp)){
			throw new CustomRunTimeException("Card already applied for associateId: " + emp.getAssociateId());
		}

		CardEntity card = new CardEntity();
		card.setEmployee(emp);
		card.setCardNumber(emp.getAssociateId());

		if (emp.getPosition() == Position.G1) {
			card.setRewardLimits(Limitpoints.GOLD.getPts());
			card.setBalance(Limitpoints.GOLD.getPts());
			card.setCardType(cardCategoryRepo.getById(1));
		} else if (emp.getPosition() == Position.G3) {
			card.setRewardLimits(Limitpoints.DIAMOND.getPts());
			card.setBalance(Limitpoints.DIAMOND.getPts());
			card.setCardType(cardCategoryRepo.getById(2));
		} else if (emp.getPosition() == Position.G4) {
			card.setRewardLimits(Limitpoints.PLATINUM.getPts());
			card.setBalance(Limitpoints.PLATINUM.getPts());
			card.setCardType(cardCategoryRepo.getById(3));
		} else {
			throw new CustomIllegalArguementException("You're not Eligible");
		}

		card.setDateOfCard(LocalDate.now());
		card.setPin(1000);
		card.setStatus(CardStatus.PENDING);
		
		
	
		return modelMapper.map(cardRepo.save(card), CardDto.class);

	}

	public CardDto approveCard(EmployeeDto employeeDto) {
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(employeeDto.getAssociateId());
		if (empOptional.isEmpty()) {
			throw new CustomRunTimeException("Employee not found for associateId: " + employeeDto.getAssociateId());
		}
	
		
		
		EmployeeEntity emp = empOptional.get();
		
		Optional<CardEntity> cardOpt = cardRepo.findByEmployee_AssociateId(emp.getAssociateId());
		if (cardOpt.isEmpty()) {
			throw new CustomRunTimeException("Card not found for associateId: " + employeeDto.getAssociateId());
		}
		CardEntity card = cardOpt.get();
		LocalDate currentDate = LocalDate.now();

		LocalDate doH = emp.getDateOfHire();

		if (card == null) {
			throw new CustomIllegalArguementException("Invalid card.");
		} else if (card.getStatus() == CardStatus.REJECTED) {
			throw new CustomIllegalArguementException("Card is Rejected.");
		} else if (card.getStatus() != CardStatus.PENDING) {
			throw new CustomIllegalArguementException("Card is already approved.");
		}

		else if (doH != null && Period.between(doH, currentDate).getYears() < 1) {
			card.setStatus(CardStatus.REJECTED);
			

			modelMapper.map(cardRepo.save(card), CardDto.class);
			throw new CustomIllegalArguementException(
					"Card cannot be approved since employment under company is less than 1 year.");

		}
		card.setEmployee(emp);
		card.setStatus(CardStatus.ACTIVE);

		
		return modelMapper.map(cardRepo.save(card), CardDto.class);

	}

	public CardDto rejectCard(EmployeeDto employee) {
		Optional<EmployeeEntity> empOptional = employeeRepo.findByAssociateId(employee.getAssociateId());
		if (empOptional.isEmpty()) {
			throw new CustomRunTimeException("Employee not found for associateId: " + employee.getAssociateId());
		}
		
		EmployeeEntity emp = empOptional.get();
		Optional<CardEntity> cardOpt = cardRepo.findByEmployee_AssociateId(emp.getAssociateId());
		if (cardOpt.isEmpty()) {
			throw new CustomRunTimeException("Card not found for associateId: " + employee.getAssociateId());
		}
		CardEntity card = cardOpt.get();
		LocalDate currentDate = LocalDate.now();
		LocalDate doH = emp.getDateOfHire();

		if (card.getStatus() == CardStatus.REJECTED) {
			throw new CustomIllegalArguementException("Card is already Rejected.");
		}

		else if (card.getStatus() == CardStatus.ACTIVE) {
			card.setStatus(CardStatus.PENDING);
			
			modelMapper.map(cardRepo.save(card), CardDto.class);
			throw new CustomIllegalArguementException("Card is Blocked Successfully.");

		}

		else if (card.getStatus() != CardStatus.PENDING && card.getStatus() != CardStatus.REJECTED) {
			throw new CustomIllegalArguementException("Card is already approved.");
		}
		card.setEmployee(emp);
		card.setStatus(CardStatus.REJECTED);

		return modelMapper.map(cardRepo.save(card), CardDto.class);

	}

	@Transactional
	public RewardPointsDto monthlyRewards() {
		List<PerfData> allPerfData = perfRepo.findAll();
		RewardPoints rew = new RewardPoints();
		for(PerfData perfData : allPerfData) {
			int daysWorked = perfData.getDaysWorked();
			
			EmployeeEntity emp = perfData.getEmployee();
			
			if(emp == null) {
                new CustomIllegalArguementException("No employee data");

			}
						
			Optional<CardEntity> cardOpt = cardRepo.findByEmployee_AssociateId(emp.getAssociateId());
			if (cardOpt.isEmpty()) {
				throw new CustomRunTimeException("Card not found for associateId: " + emp.getAssociateId());
			}
			CardEntity card = cardOpt.get();
			
			if(card == null) {
                new CustomIllegalArguementException("No card for employee");

			}
		
			
			double rewards = perfData.getPerformance().getPerf() *  card.getCardType().getPointMultiplier(); 
			
			card.setBalance(card.getBalance() + (int)rewards);
			modelMapper.map(cardRepo.save(card), CardDto.class);

			
			
			Date currentDate = new Date();
			
			
			rew.setMonthlyawards((int) rewards);
			rew.setAwardedDate(currentDate);
			rew.setCard(card);
			
			modelMapper.map(rewardPointsRepo.save(rew), RewardPointsDto.class);
		
	
		}
		return modelMapper.map(rewardPointsRepo.save(rew), RewardPointsDto.class);
		
	}
	
	
	
	
	
	
	public void mdata(MultipartFile file) throws Exception {
	    try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
	        reader.readNext(); // Skips the headers first line

	        String[] line;
	        while ((line = reader.readNext()) != null) {
	            // Print the line for debugging purposes
	            System.out.println("Processing line: " + Arrays.toString(line));

	            // Check for blank lines
	            if (line.length == 0 || Arrays.stream(line).allMatch(String::isEmpty)) {
	                System.out.println("Skipping blank line: " + Arrays.toString(line));
	                continue; // Skip blank lines
	            }

	            // Check if the line has enough columns
	            if (line.length < 3) {
	                System.out.println("Skipping line due to insufficient columns: " + Arrays.toString(line));
	                continue; // Skip this iteration if there aren't enough columns
	            }

	            // Validate and parse associateId
	            String associateIdStr = line[0].trim(); // Trim whitespace
	            if (associateIdStr.isEmpty()) {
	                System.out.println("Associate ID is empty. Skipping line: " + Arrays.toString(line));
	                continue; // Skip this iteration if associateId is empty
	            }
	            Integer associateId = Integer.parseInt(associateIdStr);

	            // Validate and parse daysWorked
	            String daysWorkedStr = line[1].trim(); // Trim whitespace
	            if (daysWorkedStr.isEmpty()) {
	                System.out.println("Days Worked is empty. Skipping line: " + Arrays.toString(line));
	                continue; // Skip this iteration if daysWorked is empty
	            }
	            Integer daysWorked = Integer.parseInt(daysWorkedStr);

	            // Get performance and ensure it's valid
	            String performanceStr = line[2].trim().toUpperCase(); // Trim whitespace
	            if (performanceStr.isEmpty()) {
	                System.out.println("Performance is empty. Skipping line: " + Arrays.toString(line));
	                continue; // Skip this iteration if performance is empty
	            }

	            try {
	                Performance performance = Performance.valueOf(performanceStr);
	                Optional<EmployeeEntity> employeeOpt = employeeRepo.findByAssociateId(associateId);
	                if (employeeOpt.isPresent()) {
	                    EmployeeEntity employee = employeeOpt.get();
	                    System.out.println("Found employee with ID: " + employee.getAssociateId());

	                    PerfData perfData = employee.getPerfData();
	                    if (perfData != null) {
	                        perfData.setDaysWorked(daysWorked);
	                        perfData.setPerformance(performance);
	                        perfRepo.save(perfData);
	                    } else {
	                        PerfData perfDa = new PerfData();
	                        perfDa.setEmployee(employee);
	                        perfDa.setDaysWorked(daysWorked);
	                        perfDa.setPerformance(performance);
	                        perfRepo.save(perfDa);
	                    }
	                } else {
	                    throw new CustomIllegalArguementException("Employee with associateId " + associateId + " not found.");
	                }
	            } catch (IllegalArgumentException e) {
	                System.out.println("Invalid performance value: " + performanceStr + ". Skipping line: " + Arrays.toString(line));
	                continue; // Skip this iteration if performance value is invalid
	            }
	        }
	    }
	}

	
	
	
	
	
	
	
	
	
	
	

}
