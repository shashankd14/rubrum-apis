package com.steel.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.entity.Instruction;

@Service
public class InstructionServiceImpl implements InstructionService {

	@Autowired
	private InstructionRepository instructionRepository;
	
	@Override
	public List<Instruction> getAll() {
		return instructionRepository.findAll();
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
		instructionRepository.save(instruction);
	}

	@Override
	public void deleteById(int id) {
		instructionRepository.deleteById(id);
	}

}
