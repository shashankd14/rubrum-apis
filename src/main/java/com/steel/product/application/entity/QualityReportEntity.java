package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.QualityReportResponse;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "quality_report")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QualityReportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inspection_id")
	private Integer inspectionId;

	@Column(name = "coil_number")
	private String coilNumber;
	
	@Column(name = "inward_id")
	private Integer inwardId;
	
	@Column(name = "template_id")
	private Integer templateId;
	
	@Column(name = "stage_name")
	private String stageName;

	@Column(name = "template_details", length = 2000)
	private String templateDetails;
	
	@Column(name = "rust_observed" )
	private String rustObserved;
	
	@Column(name = "safety_issues" )
	private String safetyIssues;
	
	@Column(name = "water_exposure" )
	private String waterExposure;
	
	@Column(name = "wire_Rope_damages" )
	private String wireRopeDamages;
	
	@Column(name = "packing_intact" )
	private String packingIntact;
	
	@Column(name = "improper_storage" )
	private String improperStorage;
	
	@Column(name = "strapping" )
	private String strapping;
	
	@Column(name = "weighment_slip" )
	private String weighmentSlip;
	
	@Column(name = "weighment" )
	private String weighment;
	
	@Column(name = "ack_receipt" )
	private String ackReceipt;
	
	@Column(name = "unloading_improper" )
	private String unloadingImproper;
	
	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	public static QualityReportResponse valueOf(QualityReportEntity entity){
		QualityReportResponse dtoResponse = new QualityReportResponse();
		dtoResponse.setInspectionId( entity.getInspectionId());
		dtoResponse.setCoilNumber( entity.getCoilNumber());
		dtoResponse.setInwardId( entity.getInwardId());
		dtoResponse.setTemplateId( entity.getTemplateId());
		dtoResponse.setStageName(entity.getStageName());
		dtoResponse.setTemplateDetails(entity.getTemplateDetails());
		dtoResponse.setRustObserved(entity.getRustObserved());
		dtoResponse.setSafetyIssues(entity.getSafetyIssues());		
		dtoResponse.setWaterExposure(entity.getWaterExposure());		
		dtoResponse.setWireRopeDamages(entity.getWireRopeDamages());		
		dtoResponse.setPackingIntact(entity.getPackingIntact());		
		dtoResponse.setImproperStorage(entity.getImproperStorage());		
		dtoResponse.setStrapping(entity.getStrapping());		
		dtoResponse.setWeighmentSlip(entity.getWeighmentSlip());		
		dtoResponse.setWeighment(entity.getWeighment());		
		dtoResponse.setAckReceipt(entity.getAckReceipt());
		dtoResponse.setUnloadingImproper(entity.getUnloadingImproper());		
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}