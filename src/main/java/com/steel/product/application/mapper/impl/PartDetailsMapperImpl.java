package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.dto.partDetails.PartDetailsRequest;
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
    public PartDetailsRequest toRequestDto(PartDetails partDetails) {
        if ( partDetails == null ) {
            return null;
        }
        PartDetailsRequest PartDetailsRequest = new PartDetailsRequest();
        PartDetailsRequest.setTargetWeight( partDetails.getTargetWeight() );
        PartDetailsRequest.setLength( partDetails.getLength() );
        PartDetailsRequest.setCreatedBy( partDetails.getCreatedBy() );
        PartDetailsRequest.setUpdatedBy( partDetails.getUpdatedBy() );
        PartDetailsRequest.setIsDeleted( partDetails.getIsDeleted() );
        return PartDetailsRequest;
    }

    @Override
    public PartDetails toEntityForSlit(PartDetailsRequest PartDetailsRequest) {
        if ( PartDetailsRequest == null ) {
            return null;
        }

        PartDetails partDetails = new PartDetails();

        if ( PartDetailsRequest.getIsDeleted() != null ) {
            partDetails.setIsDeleted( PartDetailsRequest.getIsDeleted() );
        }
        else {
            partDetails.setIsDeleted( false );
        }
        if (PartDetailsRequest.getPlannedYieldLossRatio() != null) {
			partDetails.setPlannedYieldLossRatio(PartDetailsRequest.getPlannedYieldLossRatio());
		}
        if (PartDetailsRequest.getActualYieldLossRatio() != null) {
			partDetails.setActualYieldLossRatio(PartDetailsRequest.getActualYieldLossRatio());
		}
        partDetails.setTargetWeight( PartDetailsRequest.getTargetWeight() );
        partDetails.setLength( PartDetailsRequest.getLength() );
        partDetails.setCreatedBy( PartDetailsRequest.getCreatedBy() );
        partDetails.setUpdatedBy( PartDetailsRequest.getUpdatedBy() );

        return partDetails;
    }

    @Override
    public PartDetails toEntityForCut(PartDetailsRequest PartDetailsRequest) {
		if (PartDetailsRequest == null) {
			return null;
		}
		PartDetails partDetails = new PartDetails();
		if (PartDetailsRequest.getIsDeleted() != null) {
			partDetails.setIsDeleted(PartDetailsRequest.getIsDeleted());
		} else {
			partDetails.setIsDeleted(false);
		}
		if (PartDetailsRequest.getPlannedYieldLossRatio() != null) {
			partDetails.setPlannedYieldLossRatio(PartDetailsRequest.getPlannedYieldLossRatio());
		}
		if (PartDetailsRequest.getActualYieldLossRatio() != null) {
			partDetails.setActualYieldLossRatio(PartDetailsRequest.getActualYieldLossRatio());
		}
        partDetails.setCreatedBy( PartDetailsRequest.getCreatedBy() );
        partDetails.setUpdatedBy( PartDetailsRequest.getUpdatedBy() );
        return partDetails;
    }

	@Override
	public PartDetailsResponse toResponseDto(PartDetails partDetails) {
		if (partDetails == null) {
			return null;
		}
		PartDetailsResponse partDetailsResponse = new PartDetailsResponse();
		partDetailsResponse.setInstructions(instructionSetToInstructionResponseDtoSet(partDetails.getInstructions()));
		partDetailsResponse.setId(partDetails.getId());
		partDetailsResponse.setTargetWeight(partDetails.getTargetWeight());
		partDetailsResponse.setLength(partDetails.getLength());
		partDetailsResponse.setPartDetailsId(partDetails.getPartDetailsId());
		partDetailsResponse.setPlannedYieldLossRatio(partDetails.getPlannedYieldLossRatio());
		partDetailsResponse.setActualYieldLossRatio(partDetails.getActualYieldLossRatio());
		partDetailsResponse.setCreatedBy(partDetails.getCreatedBy());
		partDetailsResponse.setUpdatedBy(partDetails.getUpdatedBy());
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
        partDetailsPdfResponse.setPlannedYieldLossRatio(partDetails.getPlannedYieldLossRatio());
        partDetailsPdfResponse.setActualYieldLossRatio(partDetails.getActualYieldLossRatio());
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
