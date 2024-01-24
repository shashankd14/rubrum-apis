package com.steel.product.application.dto.quality;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class QIRPanDetailsJsonArrayDTO {

	private Integer id;

	private String processType;
	private String finalJudgement;
	private String qualityEngineer;
	private String qualityHead;

	private List<QIRPanDetailsJsonArrayChildDTO> slitInspectionData = new ArrayList<>();
	private List<QIRPanDetailsJsonArrayChildDTO> cutInspectionData = new ArrayList<>();
	private List<QIRPanDetailsJsonArrayChildDTO> finalInspectionData = new ArrayList<>();

	private List<QIRPanToleranceChildDTO> toleranceInspectionDataCut = new ArrayList<>();
	private List<QIRPanToleranceChildDTO> toleranceInspectionDataSlit = new ArrayList<>();
}