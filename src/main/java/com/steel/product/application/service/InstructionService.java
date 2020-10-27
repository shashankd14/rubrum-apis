package com.steel.product.application.service;

import java.util.List;

import com.steel.product.application.entity.Instruction;

public interface InstructionService {

	public List<Instruction> getAll();
	
	public Instruction getById(int theId);
	
	public void save(Instruction instruction);
	
	public void deleteById(int id);
}
