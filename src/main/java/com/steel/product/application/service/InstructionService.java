package com.steel.product.application.service;

import com.steel.product.application.entity.Instruction;

import java.util.List;

public interface InstructionService {

	public List<Instruction> getAll();
	
	public Instruction getById(int theId);
	
	public void save(Instruction instruction);
	
	public void deleteById(int id);

	public void updateInstructionWithDeliveryInfo(int instructionId, int deliveryId, String remarks, int rateId);

}
