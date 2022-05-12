package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.delivery.DeliveryResponseDto;
import com.steel.product.application.dto.instruction.InstructionRequestDto;
import com.steel.product.application.dto.instruction.InstructionResponseDto;
import com.steel.product.application.dto.pdf.InstructionResponsePdfDto;
import com.steel.product.application.dto.process.ProcessDto;
import com.steel.product.application.dto.status.StatusDto;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Process;
import com.steel.product.application.entity.Status;
import com.steel.product.application.mapper.InstructionMapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:32+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class InstructionMapperImpl implements InstructionMapper {

    @Override
    public Instruction toEntity(InstructionRequestDto instructionRequestDto) {
        if ( instructionRequestDto == null ) {
            return null;
        }

        Instruction instruction = new Instruction();

        if ( instructionRequestDto.getIsSlitAndCut() != null ) {
            instruction.setIsSlitAndCut( instructionRequestDto.getIsSlitAndCut() );
        }
        else {
            instruction.setIsSlitAndCut( false );
        }
        if ( instructionRequestDto.getIsDeleted() != null ) {
            instruction.setIsDeleted( instructionRequestDto.getIsDeleted() );
        }
        else {
            instruction.setIsDeleted( false );
        }
        instruction.setInstructionDate( instructionRequestDto.getInstructionDate() );
        instruction.setPlannedLength( instructionRequestDto.getPlannedLength() );
        instruction.setActualLength( instructionRequestDto.getActualLength() );
        instruction.setPlannedWidth( instructionRequestDto.getPlannedWidth() );
        instruction.setActualWidth( instructionRequestDto.getActualWidth() );
        instruction.setPlannedWeight( instructionRequestDto.getPlannedWeight() );
        instruction.setActualWeight( instructionRequestDto.getActualWeight() );
        instruction.setPlannedNoOfPieces( instructionRequestDto.getPlannedNoOfPieces() );
        instruction.setActualNoOfPieces( instructionRequestDto.getActualNoOfPieces() );
        instruction.setParentGroupId( instructionRequestDto.getParentGroupId() );
        instruction.setWastage( instructionRequestDto.getWastage() );
        instruction.setDamage( instructionRequestDto.getDamage() );
        instruction.setPackingWeight( instructionRequestDto.getPackingWeight() );
        instruction.setRemarks( instructionRequestDto.getRemarks() );
        instruction.setCreatedBy( instructionRequestDto.getCreatedBy() );
        instruction.setUpdatedBy( instructionRequestDto.getUpdatedBy() );
        instruction.setCreatedOn( instructionRequestDto.getCreatedOn() );
        instruction.setUpdatedOn( instructionRequestDto.getUpdatedOn() );

        return instruction;
    }

    @Override
    public InstructionResponseDto toResponseDto(Instruction instruction) {
        if ( instruction == null ) {
            return null;
        }

        InstructionResponseDto instructionResponseDto = new InstructionResponseDto();

        instructionResponseDto.setProcess( processToProcessDto( instruction.getProcess() ) );
        instructionResponseDto.setStatus( statusToStatusDto( instruction.getStatus() ) );
        instructionResponseDto.setInwardEntryId( instructionInwardIdInwardEntryId( instruction ) );
        instructionResponseDto.setParentInstructionId( instructionParentInstructionInstructionId( instruction ) );
        instructionResponseDto.setInstructionId( instruction.getInstructionId() );
        instructionResponseDto.setInstructionDate( instruction.getInstructionDate() );
        instructionResponseDto.setPlannedLength( instruction.getPlannedLength() );
        instructionResponseDto.setPlannedWidth( instruction.getPlannedWidth() );
        instructionResponseDto.setPlannedWeight( instruction.getPlannedWeight() );
        instructionResponseDto.setPlannedNoOfPieces( instruction.getPlannedNoOfPieces() );
        instructionResponseDto.setActualLength( instruction.getActualLength() );
        instructionResponseDto.setActualWidth( instruction.getActualWidth() );
        instructionResponseDto.setActualWeight( instruction.getActualWeight() );
        instructionResponseDto.setActualNoOfPieces( instruction.getActualNoOfPieces() );
        instructionResponseDto.setPacketClassification( instruction.getPacketClassification() );
        instructionResponseDto.setEndUserTagsentity( instruction.getEndUserTagsEntity() );
        instructionResponseDto.setGroupId( instruction.getGroupId() );
        instructionResponseDto.setParentGroupId( instruction.getParentGroupId() );
        instructionResponseDto.setWastage( instruction.getWastage() );
        instructionResponseDto.setDamage( instruction.getDamage() );
        instructionResponseDto.setPackingWeight( instruction.getPackingWeight() );
        instructionResponseDto.setCreatedBy( instruction.getCreatedBy() );
        instructionResponseDto.setUpdatedBy( instruction.getUpdatedBy() );
        instructionResponseDto.setCreatedOn( instruction.getCreatedOn() );
        instructionResponseDto.setUpdatedOn( instruction.getUpdatedOn() );
        instructionResponseDto.setIsDeleted( instruction.getIsDeleted() );
        instructionResponseDto.setRemarks( instruction.getRemarks() );
        instructionResponseDto.setIsSlitAndCut( instruction.getIsSlitAndCut() );

        return instructionResponseDto;
    }

    @Override
    public List<InstructionResponseDto> toResponseDtoList(List<Instruction> instructions) {
        if ( instructions == null ) {
            return null;
        }

        List<InstructionResponseDto> list = new ArrayList<InstructionResponseDto>( instructions.size() );
        for ( Instruction instruction : instructions ) {
            list.add( toResponseDto( instruction ) );
        }

        return list;
    }

    @Override
    public InstructionResponsePdfDto toResponsePdfDto(Instruction instruction) {
        if ( instruction == null ) {
            return null;
        }

        InstructionResponsePdfDto instructionResponsePdfDto = new InstructionResponsePdfDto();

        instructionResponsePdfDto.setProcess( processToProcessDto( instruction.getProcess() ) );
        instructionResponsePdfDto.setInwardEntryId( instructionInwardIdInwardEntryId( instruction ) );
        instructionResponsePdfDto.setInstructionId( instruction.getInstructionId() );
        instructionResponsePdfDto.setPlannedLength( instruction.getPlannedLength() );
        instructionResponsePdfDto.setPlannedWidth( instruction.getPlannedWidth() );
        instructionResponsePdfDto.setPlannedWeight( instruction.getPlannedWeight() );
        instructionResponsePdfDto.setPlannedNoOfPieces( instruction.getPlannedNoOfPieces() );
        instructionResponsePdfDto.setActualLength( instruction.getActualLength() );
        instructionResponsePdfDto.setActualWidth( instruction.getActualWidth() );
        instructionResponsePdfDto.setActualWeight( instruction.getActualWeight() );
        instructionResponsePdfDto.setActualNoOfPieces( instruction.getActualNoOfPieces() );
        instructionResponsePdfDto.setRemarks( instruction.getRemarks() );
        instructionResponsePdfDto.setPacketClassification( instruction.getPacketClassification() );
        instructionResponsePdfDto.setEndUserTagsEntity( instruction.getEndUserTagsEntity());
        instructionResponsePdfDto.setDeliveryDetails( deliveryDetailsToDeliveryResponseDto( instruction.getDeliveryDetails() ) );

        return instructionResponsePdfDto;
    }

    protected ProcessDto processToProcessDto(Process process) {
        if ( process == null ) {
            return null;
        }

        ProcessDto processDto = new ProcessDto();

        processDto.setProcessId( process.getProcessId() );
        processDto.setProcessName( process.getProcessName() );

        return processDto;
    }

    protected StatusDto statusToStatusDto(Status status) {
        if ( status == null ) {
            return null;
        }

        StatusDto statusDto = new StatusDto();

        statusDto.setStatusId( status.getStatusId() );
        statusDto.setStatusName( status.getStatusName() );

        return statusDto;
    }

    private Integer instructionInwardIdInwardEntryId(Instruction instruction) {
        if ( instruction == null ) {
            return null;
        }
        InwardEntry inwardId = instruction.getInwardId();
        if ( inwardId == null ) {
            return null;
        }
        int inwardEntryId = inwardId.getInwardEntryId();
        return inwardEntryId;
    }

    private Integer instructionParentInstructionInstructionId(Instruction instruction) {
        if ( instruction == null ) {
            return null;
        }
        Instruction parentInstruction = instruction.getParentInstruction();
        if ( parentInstruction == null ) {
            return null;
        }
        Integer instructionId = parentInstruction.getInstructionId();
        if ( instructionId == null ) {
            return null;
        }
        return instructionId;
    }

    protected DeliveryResponseDto deliveryDetailsToDeliveryResponseDto(DeliveryDetails deliveryDetails) {
        if ( deliveryDetails == null ) {
            return null;
        }

        DeliveryResponseDto deliveryResponseDto = new DeliveryResponseDto();

        deliveryResponseDto.setDeliveryId( deliveryDetails.getDeliveryId() );
        deliveryResponseDto.setVehicleNo( deliveryDetails.getVehicleNo() );
        deliveryResponseDto.setTotalWeight( deliveryDetails.getTotalWeight() );
        deliveryResponseDto.setCreatedBy( deliveryDetails.getCreatedBy() );
        deliveryResponseDto.setUpdatedBy( deliveryDetails.getUpdatedBy() );
        deliveryResponseDto.setCreatedOn( deliveryDetails.getCreatedOn() );
        deliveryResponseDto.setUpdatedOn( deliveryDetails.getUpdatedOn() );
        deliveryResponseDto.setDeleted( deliveryDetails.getDeleted() );
        deliveryResponseDto.setCustomerInvoiceNo( deliveryDetails.getCustomerInvoiceNo() );
        deliveryResponseDto.setCustomerInvoiceDate( deliveryDetails.getCustomerInvoiceDate() );

        return deliveryResponseDto;
    }
}
