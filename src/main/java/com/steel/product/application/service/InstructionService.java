package com.steel.product.application.service;

import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.Status;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InstructionService {

	public List<Instruction> getAll();

	public List<Instruction> getAllWIP();

	public List<Instruction> getAllWIPList();
	
	public Instruction getById(int theId);
	
	public Instruction save(Instruction instruction);

	public ResponseEntity<Object> addInstruction(List<InstructionRequestDto> instructionDTOs);
	
	public void deleteById(Instruction deleteInstruction);

	public List<Instruction> findAllByGroupId(Integer groupId);

	public List<Instruction> findAllByParentGroupId(Integer parentGroupId);

	public List<Instruction> findAllByParentInstructionId(Integer parentInstructionId);

	public void updateInstructionWithDeliveryRemarks(int deliveryId, String remarks,int instructionId);

	public List<Instruction> saveAll(List<Instruction> instructions);

	public List<Instruction> findInstructionsByInstructionIdInAndStatusNot(List<Integer> ids, Status status);

	public List<Instruction> findInstructionsWithDeliveryDetails(List<Integer> instructionIds);


	ResponseEntity<Object> updateInstruction(InstructionFinishDto instructionFinishDto);
}
