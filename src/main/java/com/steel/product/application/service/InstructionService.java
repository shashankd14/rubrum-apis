package com.steel.product.application.service;

import com.steel.product.application.dto.instruction.*;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.mapper.CutInstruction;
import com.steel.product.application.mapper.TotalLengthAndWeight;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InstructionService {

	public List<Instruction> getAll();

    public List<Instruction> getAllWIP();

    public List<Instruction> getAllWIPList();

    public Instruction getById(int theId);

    public ResponseEntity<Object> addCutInstruction(List<InstructionSaveRequestDto> cutInstructionSaveRequestDtos);

    public List<Instruction> findSlitAndCutInstructionByInwardId(Integer inwardId);

    public void deleteById(Instruction deleteInstruction);

    public List<Instruction> findAllByGroupId(Integer groupId);

    public List<Instruction> findAllByParentGroupId(Integer parentGroupId);

    public List<Instruction> findAllByParentInstructionId(Integer parentInstructionId);

    public void updateInstructionWithDeliveryRemarks(int deliveryId, String remarks, int instructionId);

    public Instruction findInstructionById(Integer instructionId);

    public List<Instruction> saveAll(List<Instruction> instructions);

    public Instruction save(Instruction instruction);

    public InstructionResponseDto saveUnprocessedForDelivery(Integer inwardId);

    ResponseEntity<Object> updateInstruction(InstructionFinishDto instructionFinishDto);

    public List<Instruction> findAllByInstructionIdInAndStatus(List<Integer> instructionIds, Integer statusId);

    public Float sumOfPlannedWeightOfInstructionsHavingGroupId(Integer groupId);

    public Float sumOfPlannedWeightOfInstructionHavingParentInstructionId(Integer parentInstructionId);

    public Float sumOfPlannedLengthOfInstructionHavingParentInstructionId(Integer parentInstructionId);

    TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingParentInstructionId(Integer parentInstructionId);

    TotalLengthAndWeight sumOfPlannedLengthAndWeightOfInstructionsHavingGroupId(Integer groupId);

    List<Instruction> getAllByInstructionIdIn(List<Integer> instructionIds);

    ResponseEntity<Object> addSlitInstruction(List<InstructionSaveRequestDto> slitInstructionSaveRequestDtos);

    ResponseEntity<Object> addInstruction(List<InstructionSaveRequestDto> instructionSaveRequestDtos);

    InwardEntryPdfDto findInwardJoinFetchInstructionsAndPartDetails(String partDetailsId);

    List<CutInstruction> findCutInstructionsByParentGroupId(Integer inwardId, Integer groupId, Integer processId);

}

