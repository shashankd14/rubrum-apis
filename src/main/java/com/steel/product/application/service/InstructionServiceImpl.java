package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
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

	@Autowired
	private PacketClassificationService packetClassificationService;

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
	public ResponseEntity<Object> addInstruction(List<InstructionRequestDto> InstructionRequestDtos) {
		float incomingWeight = 0f;
		float availableWeight = 0f;
		float existingWeight = 0f;
		boolean fromInward = false, fromParentInstruction = false, fromGroup = false;
		InwardEntry inwardEntry = null;
		Instruction parentInstruction = null;

		if(InstructionRequestDtos.get(0).getInwardId() != null){
			incomingWeight = (float) InstructionRequestDtos.stream().mapToDouble(InstructionRequestDto::getPlannedWeight).sum();
			inwardEntry = inwardService.getByEntryId(InstructionRequestDtos.get(0).getInwardId());
			availableWeight = inwardEntry.getFpresent();
			fromInward = true;
		}else if(InstructionRequestDtos.get(0).getParentInstructionId() != null)
		{
			incomingWeight = (float)InstructionRequestDtos.stream().
					mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			existingWeight = (float)findAllByParentInstructionId(InstructionRequestDtos.get(0).getParentInstructionId())
					.stream().mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			parentInstruction = getById(InstructionRequestDtos.get(0).getParentInstructionId());
			availableWeight = parentInstruction.getActualWeight() != null ? parentInstruction.getActualWeight() : parentInstruction.getPlannedWeight();
			fromParentInstruction = true;
		}
		else if(InstructionRequestDtos.get(0).getGroupId() != null){
			List<Instruction> existingInstructions = findAllByParentGroupId(InstructionRequestDtos.get(0).getGroupId());
			if(existingInstructions.size() > 0){
				return new ResponseEntity<Object>("Instructions with parent group id "+InstructionRequestDtos.get(0).getGroupId()+" already exists.", HttpStatus.BAD_REQUEST);
			}
			incomingWeight = (float)InstructionRequestDtos.stream().
					mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			availableWeight = (float)findAllByGroupId(InstructionRequestDtos.get(0).getGroupId()).stream()
					.mapToDouble(i -> i.getActualWeight() != null ? i.getActualWeight() : i.getPlannedWeight()).sum();
			fromGroup = true;
		}else{
			return new ResponseEntity<Object>("Invalid request.",HttpStatus.BAD_REQUEST);
		}

		if(incomingWeight > availableWeight - existingWeight){
			return new ResponseEntity<Object>("No available weight for processing.", HttpStatus.BAD_REQUEST);
		}
		if(fromGroup && incomingWeight != availableWeight){
			return new ResponseEntity<Object>("Input instructions total weight must be equal to instructions in group id "+InstructionRequestDtos.get(0).getGroupId(),HttpStatus.BAD_REQUEST);
		}

		List<Instruction> savedInstructionList = new ArrayList<Instruction>();
		try {
			for (InstructionRequestDto InstructionRequestDto : InstructionRequestDtos) {

				Instruction instruction = new Instruction();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				instruction.setInstructionId(0);
				instruction.setProcess(processService.getById(InstructionRequestDto.getProcessId()));
				instruction.setInstructionDate(InstructionRequestDto.getInstructionDate());

				if (InstructionRequestDto.getPlannedLength() != null)
					instruction.setPlannedLength(InstructionRequestDto.getPlannedLength());

				if (InstructionRequestDto.getPlannedWidth() != null)
					instruction.setPlannedWidth(InstructionRequestDto.getPlannedWidth());

				if (InstructionRequestDto.getPlannedWeight() != null)
					instruction.setPlannedWeight(InstructionRequestDto.getPlannedWeight());

				if (InstructionRequestDto.getPlannedNoOfPieces() != null)
					instruction.setPlannedNoOfPieces(InstructionRequestDto.getPlannedNoOfPieces());

				instruction.setStatus(statusService.getStatusById(2));

				if (InstructionRequestDto.getWastage() != null)
					instruction.setWastage(InstructionRequestDto.getWastage());

				if (InstructionRequestDto.getDamage() != null)
					instruction.setDamage(InstructionRequestDto.getDamage());

				if (InstructionRequestDto.getPackingWeight() != null)
					instruction.setPackingWeight(InstructionRequestDto.getPackingWeight());

				instruction.setActualLength(null);
				instruction.setActualWeight(null);
				instruction.setActualWidth(null);
				instruction.setActualNoOfPieces(null);

				instruction.setCreatedBy(InstructionRequestDto.getCreatedBy());
				instruction.setUpdatedBy(InstructionRequestDto.getUpdatedBy());
				instruction.setCreatedOn(timestamp);
				instruction.setUpdatedOn(timestamp);
				instruction.setIsDeleted(false);

				if(fromParentInstruction){
					parentInstruction.addChildInstruction(instruction);
				}else if(fromInward){
					inwardEntry.setStatus(statusService.getStatusById(2));
					inwardEntry.addInstruction(instruction);
				}else{
					instruction.setParentGroupId(InstructionRequestDto.getGroupId());
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
			savedInstructionList = saveAll(savedInstructionList);
		}
		return new ResponseEntity<>(savedInstructionList.stream().map(i -> Instruction.valueOf(i)),HttpStatus.OK);
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

	@Override
	public ResponseEntity<Object> updateInstruction(InstructionFinishDto instructionFinishDto) {
		List<InstructionRequestDto> InstructionRequestDtos = instructionFinishDto.getInstructionRequestDtos();
		List<Instruction> updatedInstructionList = new ArrayList<Instruction>();
		Instruction updatedInstruction = new Instruction();
		InwardEntry inwardEntry = new InwardEntry();
		Instruction instruction;

		if(instructionFinishDto.getIsCoilFinished() && instructionFinishDto.getCoilNumber()!=null){
			inwardEntry = inwardService.getByCoilNumber(instructionFinishDto.getCoilNumber());
			inwardEntry.setStatus(statusService.getStatusById(3));
			inwardService.saveEntry(inwardEntry);
		}
		for (InstructionRequestDto InstructionRequestDto : InstructionRequestDtos) {
			try {

				instruction = getById(InstructionRequestDto.getInstructionId());
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				if (InstructionRequestDto.getInwardId() != null) {

					InwardEntry inward = inwardService.getByEntryId(InstructionRequestDto.getInwardId());
					instruction.setInwardId(inward);
				}

				instruction.setProcess(processService.getById(InstructionRequestDto.getProcessId()));
				instruction.setInstructionDate(InstructionRequestDto.getInstructionDate());

				if (InstructionRequestDto.getPlannedLength() != null)
					instruction.setPlannedLength(InstructionRequestDto.getPlannedLength());

				if (InstructionRequestDto.getActualLength() != null)
					instruction.setActualLength(InstructionRequestDto.getActualLength());

				if (InstructionRequestDto.getPlannedWidth() != null)
					instruction.setPlannedWidth(InstructionRequestDto.getPlannedWidth());

				if (InstructionRequestDto.getActualWidth() != null)
					instruction.setActualWidth(InstructionRequestDto.getActualWidth());

				if (InstructionRequestDto.getActualWeight() != null)
					instruction.setActualWeight(InstructionRequestDto.getActualWeight());

				if (InstructionRequestDto.getPlannedNoOfPieces() != null)
					instruction.setPlannedNoOfPieces(InstructionRequestDto.getPlannedNoOfPieces());

				if (InstructionRequestDto.getActualNoOfPieces() != null)
					instruction.setActualNoOfPieces(InstructionRequestDto.getActualNoOfPieces());

				if (InstructionRequestDto.getStatus() != null)
					instruction.setStatus(statusService.getStatusById(InstructionRequestDto.getStatus()));

				if (InstructionRequestDto.getPacketClassificationId() != null)
					instruction.setPacketClassification(packetClassificationService.getPacketClassificationById(InstructionRequestDto.getPacketClassificationId()));

				if (InstructionRequestDto.getGroupId() != null)
					instruction.setGroupId(InstructionRequestDto.getGroupId());

				if (InstructionRequestDto.getParentInstructionId() != null) {

					Instruction parentInstruction = getById(InstructionRequestDto.getParentInstructionId());

					instruction.setParentInstruction(parentInstruction);
				} else
					instruction.setParentInstruction(null);

				if (InstructionRequestDto.getWastage() != null)
					instruction.setWastage(InstructionRequestDto.getWastage());

				if (InstructionRequestDto.getDamage() != null)
					instruction.setDamage(InstructionRequestDto.getDamage());

				if (InstructionRequestDto.getPackingWeight() != null)
					instruction.setPackingWeight(InstructionRequestDto.getPackingWeight());

				instruction.setCreatedBy(InstructionRequestDto.getCreatedBy());
				instruction.setUpdatedBy(InstructionRequestDto.getUpdatedBy());
				instruction.setCreatedOn(timestamp);
				instruction.setUpdatedOn(timestamp);
				instruction.setIsDeleted(false);

				updatedInstruction = instructionRepository.save(instruction);
				updatedInstructionList.add(updatedInstruction);

				//	return new ResponseEntity<Object>("update success!!", HttpStatus.OK);
			} catch (Exception e) {

				return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<Object>(updatedInstructionList.stream().map(i -> Instruction.valueOf(i)), HttpStatus.OK);
	}

	@Override
	public List<Instruction> findByIdIn(List<Integer> ids) {
		return instructionRepository.findByInstructionIdIn(ids);
	}



}
