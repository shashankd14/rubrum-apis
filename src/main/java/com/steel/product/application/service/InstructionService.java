package com.steel.product.application.service;

import java.util.List;

import com.steel.product.application.entity.Instruction;
import org.springframework.data.repository.query.Param;

public interface InstructionService {

	public List<Instruction> getAll();
	
	public Instruction getById(int theId);
	
	public void save(Instruction instruction);
	
	public void deleteById(int id);

	public void updateInstructionWithDeliveryId(int instructionId, int deliveryId, String remarks);

}
