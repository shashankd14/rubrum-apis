package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.instruction.InstructionDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InstructionServiceImpl implements InstructionService {

	@Autowired
	private InstructionRepository instructionRepository;

	@Autowired
	private InwardEntryRepository inwardEntryRepository;

	@Autowired
	private InwardEntryService inwardService;

	@Autowired
	private ProcessService processService;

	@Autowired
	private StatusService statusService;
	
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
	public ResponseEntity<Object> addInstruction(List<InstructionDto> instructionDTOs) {
		float incomingWeight = 0f;
		float availableWeight = 0f;
		float existingWeight = 0f;
		boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
		InwardEntry inwardEntry = null;
		Instruction parentInstruction = null;

		if(instructionDTOs.get(0).getInwardId() != null){
			incomingWeight = (float) instructionDTOs.stream().mapToDouble(InstructionDto::getPlannedWeight).sum();
			inwardEntry = inwardService.getByEntryId(instructionDTOs.get(0).getInwardId());
			availableWeight = inwardEntry.getFpresent();
			fromInward = true;
		}else if(instructionDTOs.get(0).getParentInstructionId() != null)
		{
			incomingWeight = (float)instructionDTOs.stream().
					mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			existingWeight = (float)findAllByParentInstructionId(instructionDTOs.get(0).getParentInstructionId())
					.stream().mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			parentInstruction = getById(instructionDTOs.get(0).getParentInstructionId());
			availableWeight = parentInstruction.getActualWeight() != null ? parentInstruction.getActualWeight() : parentInstruction.getPlannedWeight();
			fromParentInstruction = true;
		}
		else if(instructionDTOs.get(0).getGroupId() != null){
			List<Instruction> existingInstructions = findAllByParentGroupId(instructionDTOs.get(0).getGroupId());
			if(existingInstructions.size() > 0){
				return new ResponseEntity<Object>("Instructions with parent group id "+instructionDTOs.get(0).getGroupId()+" already exists.", HttpStatus.BAD_REQUEST);
			}
			incomingWeight = (float)instructionDTOs.stream().
					mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			availableWeight = (float)findAllByGroupId(instructionDTOs.get(0).getGroupId()).stream()
					.mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			fromGroup = true;
		}else{
			return new ResponseEntity<Object>("Invalid request.",HttpStatus.BAD_REQUEST);
		}

		if(incomingWeight > availableWeight - existingWeight){
			return new ResponseEntity<Object>("No available weight for processing.", HttpStatus.BAD_REQUEST);
		}
		if(fromGroup && incomingWeight != availableWeight){
			return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions in group id "+instructionDTOs.get(0).getGroupId(),HttpStatus.BAD_REQUEST);
		}

		List<Instruction> savedInstructionList = new ArrayList<Instruction>();
		try {
			for (InstructionDto instructionDTO : instructionDTOs) {

				Instruction instruction = new Instruction();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				instruction.setInstructionId(0);
				instruction.setProcess(processService.getById(instructionDTO.getProcessId()));
				instruction.setInstructionDate(instructionDTO.getInstructionDate());

				if (instructionDTO.getPlannedLength() != null)
					instruction.setPlannedLength(instructionDTO.getPlannedLength());

				if (instructionDTO.getPlannedWidth() != null)
					instruction.setPlannedWidth(instructionDTO.getPlannedWidth());

				if (instructionDTO.getPlannedWeight() != null)
					instruction.setPlannedWeight(instructionDTO.getPlannedWeight());

				if (instructionDTO.getPlannedNoOfPieces() != null)
					instruction.setPlannedNoOfPieces(instructionDTO.getPlannedNoOfPieces());

				instruction.setStatus(statusService.getStatusById(2));

				if (instructionDTO.getWastage() != null)
					instruction.setWastage(instructionDTO.getWastage());

				if (instructionDTO.getDamage() != null)
					instruction.setDamage(instructionDTO.getDamage());

				if (instructionDTO.getPackingWeight() != null)
					instruction.setPackingWeight(instructionDTO.getPackingWeight());

				instruction.setActualLength(null);
				instruction.setActualWeight(null);
				instruction.setActualWidth(null);
				instruction.setActualNoOfPieces(null);

				instruction.setCreatedBy(instructionDTO.getCreatedBy());
				instruction.setUpdatedBy(instructionDTO.getUpdatedBy());
				instruction.setCreatedOn(timestamp);
				instruction.setUpdatedOn(timestamp);
				instruction.setIsDeleted(false);

				if(fromParentInstruction){
					parentInstruction.addChildInstruction(instruction);
				}else if(fromInward){
					inwardEntry.setStatus(statusService.getStatusById(2));
					inwardEntry.addInstruction(instruction);
				}else{
					instruction.setParentGroupId(instructionDTO.getGroupId());
				}
				savedInstructionList.add(instruction);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(fromParentInstruction){
			instructionRepository.save(parentInstruction);
		}else if(fromInward){
			inwardEntry.setFpresent(availableWeight - incomingWeight);
			inwardService.saveEntry(inwardEntry);
		}else{
			saveAll(savedInstructionList);
		}
		return new ResponseEntity<>(savedInstructionList,HttpStatus.OK);
	}

	@Override
	@Transactional
	public void deleteById(Instruction deleteInstruction) {
		deleteInstruction.setPacketClassification(null);
		deleteInstruction.setDeliveryDetails(null);
		if(deleteInstruction.getInwardId()!=null){
			InwardEntry inwardEntry = deleteInstruction.getInwardId();
			if (deleteInstruction.getPlannedWeight()!=null && inwardEntry.getFpresent() != null) {
				inwardEntry.setFpresent((inwardEntry.getFpresent() + deleteInstruction.getPlannedWeight()));
			}
			inwardEntry.removeInstruction(deleteInstruction);
			inwardEntryRepository.save(inwardEntry);
			instructionRepository.delete(deleteInstruction);
		}else if(deleteInstruction.getParentInstruction() != null){
			Instruction parentInstruction = deleteInstruction.getParentInstruction();
				parentInstruction.removeChildInstruction(deleteInstruction);
				instructionRepository.save(parentInstruction);
		}else{
			throw new RuntimeException("Cannot find inward or parent instruction from the instruction");
		}

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
