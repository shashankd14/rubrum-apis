package com.steel.product.application.mapper;

import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;
import com.steel.product.application.entity.Instruction;
import org.mapstruct.Mapping;
import java.util.List;

//@Mapper(componentModel = "spring")
public interface InstructionMapper {

    @Mapping(target = "inwardId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "process", ignore = true)
    @Mapping(target = "instructionId", ignore = true)
    @Mapping(target = "packetClassification", ignore = true)
    @Mapping(target = "isSlitAndCut", defaultValue = "false")
    @Mapping(target = "isDeleted", defaultValue = "false")
    @Mapping(target = "groupId", ignore = true)
    Instruction toEntity(InstructionRequestDto instructionRequestDto);


    @Mapping(target = "process", source = "instruction.process")
    @Mapping(target = "status", source = "instruction.status")
    @Mapping(target = "childInstructions", ignore = true)
    @Mapping(target = "deliveryDetails", ignore = true)
    @Mapping(source = "instruction.inwardId.inwardEntryId", target = "inwardEntryId")
    @Mapping(target = "parentInstructionId", source = "instruction.parentInstruction.instructionId")
    @Mapping(target = "partDetails", ignore = true)
    InstructionResponseDto toResponseDto(Instruction instruction);

    List<InstructionResponseDto> toResponseDtoList(List<Instruction> instructions);

    @Mapping(target = "process", source = "instruction.process")
    @Mapping(source = "instruction.inwardId.inwardEntryId", target = "inwardEntryId")
    @Mapping(target = "countOfWeight", ignore = true)
    InstructionResponsePdfDto toResponsePdfDto(Instruction instruction);
}
