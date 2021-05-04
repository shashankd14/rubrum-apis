package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructionServiceImpl implements InstructionService {

	@Autowired
	private InstructionRepository instructionRepository;

	@Autowired
	private InwardEntryRepository inwardEntryRepository;
	
	@Override
	public List<Instruction> getAll() {
		return instructionRepository.findAll();
	}

	@Override
	public List<Instruction> getAllWIP() {
		return instructionRepository.getAllWIP();
	}

	@Override
	public List<Instruction> getAllWIPList() {
		return instructionRepository.getAllWIPList();
	}

	@Override
	public Instruction getById(int theId) {
		
		Optional<Instruction> result = instructionRepository.findById(theId);
		
		Instruction theInstruction = null;
		
		if (result.isPresent()) {
			theInstruction = result.get();
		}
		else {
			// we didn't find the employee
			throw new RuntimeException("Did not find employee id - " + theId);
		}
		
		return theInstruction;
	}

	@Override
	public void save(Instruction instruction) {
		if (instruction.getInwardId()!=null){
			InwardEntry inwardEntry = instruction.getInwardId();
			if (instruction.getPlannedWeight()!=null && inwardEntry.getFpresent() != null)
				inwardEntry.setFpresent((inwardEntry.getFpresent()-instruction.getPlannedWeight()));
			inwardEntryRepository.save(inwardEntry);
		}

		instructionRepository.save(instruction);
	}

	@Override
	public void deleteById(int id) {
		instructionRepository.deleteById(id);
	}

	@Override
	public void updateInstructionWithDeliveryRemarks( int deliveryId,
												  String remarks, int instructionId) {
		instructionRepository.updateInstructionWithDeliveryRemarks(instructionId,deliveryId, remarks);
	}

}
