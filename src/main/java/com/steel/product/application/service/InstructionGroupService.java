package com.steel.product.application.service;

import com.steel.product.application.dto.instruction.InstructionGroupDto;
import com.steel.product.application.entity.Address;
import com.steel.product.application.entity.InstructionGroup;

import java.util.List;

public interface InstructionGroupService {

    InstructionGroup saveInstructionGroup(InstructionGroupDto instructionGroupDto);

    List<InstructionGroup> getAllInstructionGroups();

    Address getInstructionGroupById(int instructionGroupId);
}
