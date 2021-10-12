package com.steel.product.application.mapper;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.partDetailsRequest;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.PartDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InstructionMapper.class})
public interface PartDetailsMapper {

    partDetailsRequest toDto(PartDetails partDetails);

    @Mapping(target = "isDeleted", defaultValue = "false")
    PartDetails toEntity(partDetailsRequest partDetailsRequest);

    @Mapping(target = "instructionResponseDtos", source = "partDetails.instructions")
    PartDetailsResponse toResponseDtoList(PartDetails partDetails);

    List<PartDetailsResponse> toResponseDtoList(List<PartDetails> partDetails);


}
