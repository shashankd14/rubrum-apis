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
			throw new RuntimeException("Did not find instruction id - " + theId);
		}
		
		return theInstruction;
	}

	@Override
	public Instruction save(Instruction instruction) {
		Instruction savedInstruction = new Instruction();
		if (instruction.getInwardId()!=null){
			InwardEntry inwardEntry = instruction.getInwardId();
			if (instruction.getPlannedWeight()!=null && inwardEntry.getFpresent() != null)
				inwardEntry.setFpresent((inwardEntry.getFpresent()-instruction.getPlannedWeight()));
			inwardEntryRepository.save(inwardEntry);
		}

		savedInstruction = instructionRepository.save(instruction);

		return savedInstruction;
	}

	@Override
	public void deleteById(Instruction deleteInstruction) {
		if(deleteInstruction.getInwardId()!=null){
			InwardEntry inwardEntry = deleteInstruction.getInwardId();
			if (deleteInstruction.getPlannedWeight()!=null && inwardEntry.getFpresent() != null)
				inwardEntry.setFpresent((inwardEntry.getFpresent()+deleteInstruction.getPlannedWeight()));
			inwardEntryRepository.save(inwardEntry);
		}else{
			Instruction parentInstruction = deleteInstruction.getParentInstruction();
			if (deleteInstruction.getPlannedWeight()!=null && parentInstruction.getPlannedWeight() != null)
				parentInstruction.setPlannedWeight((deleteInstruction.getPlannedWeight()+parentInstruction.getPlannedWeight()));
			instructionRepository.save(parentInstruction);
		}

		instructionRepository.deleteById(deleteInstruction.getInstructionId());
	}

	@Override
	public List<Instruction> findAllByGroupId(Integer groupId) {
		return instructionRepository.findByGroupId(groupId);
	}

	@Override
	public List<Instruction> findAllByParentGroupId(Integer parentGroupId) {
		return instructionRepository.findByParentGroupId(parentGroupId);
	}

	@Override
	public List<Instruction> findAllByParentInstructionId(Integer parentInstructionId) {
		return instructionRepository.findByParentInstructionId(parentInstructionId);
	}

	@Override
	public void updateInstructionWithDeliveryRemarks( int deliveryId,
												  String remarks, int instructionId) {
		instructionRepository.updateInstructionWithDeliveryRemarks(instructionId,deliveryId, remarks);
	}

	@Override
	public List<Instruction> saveAll(List<Instruction> instructions) {
		return instructionRepository.saveAll(instructions);
	}

}
