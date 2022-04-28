package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.partDetailsRequest;
import com.steel.product.application.dto.pdf.PartDetailsPdfResponse;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.PartDetails;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.mapper.PartDetailsMapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:33+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class PartDetailsMapperImpl implements PartDetailsMapper {

    @Autowired
    private InstructionMapper instructionMapper;

    @Override
    public partDetailsRequest toRequestDto(PartDetails partDetails) {
        if ( partDetails == null ) {
            return null;
        }

        partDetailsRequest partDetailsRequest = new partDetailsRequest();

        partDetailsRequest.setTargetWeight( partDetails.getTargetWeight() );
        partDetailsRequest.setLength( partDetails.getLength() );
        partDetailsRequest.setCreatedBy( partDetails.getCreatedBy() );
        partDetailsRequest.setUpdatedBy( partDetails.getUpdatedBy() );
        partDetailsRequest.setIsDeleted( partDetails.getIsDeleted() );

        return partDetailsRequest;
    }

    @Override
    public PartDetails toEntityForSlit(partDetailsRequest partDetailsRequest) {
        if ( partDetailsRequest == null ) {
            return null;
        }

        PartDetails partDetails = new PartDetails();

        if ( partDetailsRequest.getIsDeleted() != null ) {
            partDetails.setIsDeleted( partDetailsRequest.getIsDeleted() );
        }
        else {
            partDetails.setIsDeleted( false );
        }
        partDetails.setTargetWeight( partDetailsRequest.getTargetWeight() );
        partDetails.setLength( partDetailsRequest.getLength() );
        partDetails.setCreatedBy( partDetailsRequest.getCreatedBy() );
        partDetails.setUpdatedBy( partDetailsRequest.getUpdatedBy() );

        return partDetails;
    }

    @Override
    public PartDetails toEntityForCut(partDetailsRequest partDetailsRequest) {
        if ( partDetailsRequest == null ) {
            return null;
        }

        PartDetails partDetails = new PartDetails();

        if ( partDetailsRequest.getIsDeleted() != null ) {
            partDetails.setIsDeleted( partDetailsRequest.getIsDeleted() );
        }
        else {
            partDetails.setIsDeleted( false );
        }
        partDetails.setCreatedBy( partDetailsRequest.getCreatedBy() );
        partDetails.setUpdatedBy( partDetailsRequest.getUpdatedBy() );

        return partDetails;
    }

    @Override
    public PartDetailsResponse toResponseDto(PartDetails partDetails) {
        if ( partDetails == null ) {
            return null;
        }

        PartDetailsResponse partDetailsResponse = new PartDetailsResponse();

        partDetailsResponse.setInstructions( instructionSetToInstructionResponseDtoSet( partDetails.getInstructions() ) );
        partDetailsResponse.setId( partDetails.getId() );
        partDetailsResponse.setTargetWeight( partDetails.getTargetWeight() );
        partDetailsResponse.setLength( partDetails.getLength() );
        partDetailsResponse.setPartDetailsId( partDetails.getPartDetailsId() );
        partDetailsResponse.setCreatedBy( partDetails.getCreatedBy() );
        partDetailsResponse.setUpdatedBy( partDetails.getUpdatedBy() );

        return partDetailsResponse;
    }

    @Override
    public PartDetailsPdfResponse toPartDetailsPdfResponse(PartDetails partDetails) {
        if ( partDetails == null ) {
            return null;
        }

        PartDetailsPdfResponse partDetailsPdfResponse = new PartDetailsPdfResponse();

        partDetailsPdfResponse.setId( partDetails.getId() );
        partDetailsPdfResponse.setPartDetailsId( partDetails.getPartDetailsId() );
        partDetailsPdfResponse.setTargetWeight( partDetails.getTargetWeight() );
        partDetailsPdfResponse.setLength( partDetails.getLength() );

        return partDetailsPdfResponse;
    }

    @Override
    public List<PartDetailsResponse> toResponseDto(List<PartDetails> partDetails) {
        if ( partDetails == null ) {
            return null;
        }

        List<PartDetailsResponse> list = new ArrayList<PartDetailsResponse>( partDetails.size() );
        for ( PartDetails partDetails1 : partDetails ) {
            list.add( toResponseDto( partDetails1 ) );
        }

        return list;
    }

    protected Set<InstructionResponseDto> instructionSetToInstructionResponseDtoSet(Set<Instruction> set) {
        if ( set == null ) {
            return null;
        }

        Set<InstructionResponseDto> set1 = new LinkedHashSet<InstructionResponseDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Instruction instruction : set ) {
            set1.add( instructionMapper.toResponseDto( instruction ) );
        }

        return set1;
    }
}
