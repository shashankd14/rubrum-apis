package com.steel.product.application.mapper;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.PartDetailsRequest;
import com.steel.product.application.dto.pdf.PartDetailsPdfResponse;
import com.steel.product.application.entity.PartDetails;
import org.mapstruct.Mapping;
import java.util.List;

//@Mapper(componentModel = "spring", uses = {InstructionMapper.class})
public interface PartDetailsMapper {

    PartDetailsRequest toRequestDto(PartDetails partDetails);

    @Mapping(target = "isDeleted", defaultValue = "false")
    PartDetails toEntityForSlit(PartDetailsRequest PartDetailsRequest);

    @Mapping(target = "isDeleted", defaultValue = "false")
    @Mapping(target = "targetWeight", ignore = true)
    @Mapping(target = "length", ignore = true)
    PartDetails toEntityForCut(PartDetailsRequest PartDetailsRequest);

    @Mapping(target = "instructions", source = "partDetails.instructions")
    PartDetailsResponse toResponseDto(PartDetails partDetails);

    @Mapping(target = "instructions", ignore = true)
    PartDetailsPdfResponse toPartDetailsPdfResponse(PartDetails partDetails);

    List<PartDetailsResponse> toResponseDto(List<PartDetails> partDetails);

    //List<PartDetailsResponse> toResponseDtoWithoutInstructionsInPartDetails(List<PartDetails> partDetails);

}
