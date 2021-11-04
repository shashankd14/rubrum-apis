package com.steel.product.application.mapper;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.partDetailsRequest;
import com.steel.product.application.dto.pdf.PartDetailsPdfResponse;
import com.steel.product.application.entity.PartDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InstructionMapper.class})
public interface PartDetailsMapper {

    partDetailsRequest toRequestDto(PartDetails partDetails);

    @Mapping(target = "isDeleted", defaultValue = "false")
    PartDetails toEntityForSlit(partDetailsRequest partDetailsRequest);

    @Mapping(target = "isDeleted", defaultValue = "false")
    @Mapping(target = "targetWeight", ignore = true)
    @Mapping(target = "length", ignore = true)
    PartDetails toEntityForCut(partDetailsRequest partDetailsRequest);

    @Mapping(target = "instructions", source = "partDetails.instructions")
    PartDetailsResponse toResponseDto(PartDetails partDetails);

    @Mapping(target = "instructions", ignore = true)
    PartDetailsPdfResponse toPartDetailsPdfResponse(PartDetails partDetails);

    List<PartDetailsResponse> toResponseDto(List<PartDetails> partDetails);


}
