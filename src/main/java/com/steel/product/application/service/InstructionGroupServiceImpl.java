package com.steel.product.application.service;

import com.steel.product.application.dao.InstructionGroupRepository;
import com.steel.product.application.dto.instruction.InstructionGroupDto;
import com.steel.product.application.entity.Address;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InstructionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructionGroupServiceImpl implements InstructionGroupService{

    @Autowired
    private InstructionGroupRepository instructionGroupRepository;

    @Autowired
    private InstructionService instructionService;

    @Override
    public InstructionGroup saveInstructionGroup(InstructionGroupDto instructionGroupDto) {

        InstructionGroup instructionGroup = new InstructionGroup();
        List<Instruction> instructions = instructionService.getAllByInstructionIdIn(instructionGroupDto.getInstructionId());
        InstructionGroup savedInstructionGroup = instructionGroupRepository.save(instructionGroup);
        savedInstructionGroup = this.createBundle(instructions,savedInstructionGroup);
        instructionService.saveAll(instructions);
        return instructionGroupRepository.save(savedInstructionGroup);
    }

    private InstructionGroup createBundle(List<Instruction> instructions, InstructionGroup savedInstructionGroup) {
        for(Instruction ins:instructions){
            ins.setGroupId(savedInstructionGroup.getGroupId());
        }
        savedInstructionGroup.setInstructionCount(instructions.size());
        return savedInstructionGroup;
    }

    @Override
    public List<InstructionGroup> getAllInstructionGroups() {
        return null;
    }

    @Override
    public Address getInstructionGroupById(int instructionGroupId) {
        return null;
    }

    @Override
    public void deleteGroupId(Integer groupId) {
        try {
                instructionGroupRepository.deleteGroupId(groupId);
        }catch (Exception e){
            e.getMessage();
        }
    }
}
