package com.steel.product.application.dto.instruction;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WIPChildListResponseDTO {

	private Integer inwardEntryId;

	private Integer instructionId;

	private Float plannedLength;

	private Float plannedWidth;

	private Float plannedWeight;

	private Integer plannedNoOfPieces;

	private Float actualLength;

	private Float actualWidth;

	private Float actualWeight;

	private Integer actualNoOfPieces;

	private Float additionalWeight;

	private String processName;

	private String statusName;

	private String classificationName;

	private String remarks;

	private String endUserTagName;

	private String soNo;

	private Date instructionDate;

}
