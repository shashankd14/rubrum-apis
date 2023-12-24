package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.QualityInspectionReportResponse;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "quality_inspection_report")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QualityInspectionReportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qir_id")
	private Integer qirId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private QualityTemplateEntity templateEntity=new QualityTemplateEntity();
	
	@Column(name = "coil_no")
	private String coilNo;

	@Column(name = "customer_batch_no")
	private String customerBatchNo;
	
	@Column(name = "plan_id")
	private String planId;
	
	@Column(name = "delivery_chalan_no")
	private String deliveryChalanNo;
	
	@Column(name = "stage_name")
	private String stageName;

	@Column(name = "process_id")
	private Integer processId;

	@Column(name = "template_details", length = 2000)
	private String templateDetails;

	@Column(name = "plan_details", length = 2000)
	private String planDetails;
	
	@Column(name = "rust_observed" )
	private String rustObserved;
	
	@Column(name = "coil_bend" )
	private String coilBend;
	
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

	@Column(name = "packing_damage_transit" )
	private String packingDamageTransit;

	@Column(name = "processing_report1" )
	private String processingReport1;

	@Column(name = "processing_report2" )
	private String processingReport2;

	@Column(name = "processing_report3" )
	private String processingReport3;

	@Column(name = "processing_report4" )
	private String processingReport4;

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

	public static QualityInspectionReportResponse valueOf(QualityInspectionReportEntity entity){
		QualityInspectionReportResponse dtoResponse = new QualityInspectionReportResponse();
		dtoResponse.setQirId( entity.getQirId());
		dtoResponse.setTemplateId(entity.getTemplateEntity().getTemplateId());
		dtoResponse.setTemplateName(entity.getTemplateEntity().getTemplateName());
		dtoResponse.setCoilNo(entity.getCoilNo());
		dtoResponse.setPlanId( entity.getPlanId());
		dtoResponse.setDeliveryChalanNo (entity.getDeliveryChalanNo());
		dtoResponse.setStageName(entity.getStageName());
		dtoResponse.setProcessId( entity.getProcessId() );
		dtoResponse.setTemplateDetails(entity.getTemplateDetails());
		dtoResponse.setPlanDetails( entity.getPlanDetails());
		dtoResponse.setRustObserved(entity.getRustObserved());
		dtoResponse.setCoilBend( entity.getCoilBend());
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
		dtoResponse.setPackingDamageTransit(entity.getPackingDamageTransit());	
		dtoResponse.setProcessingReport1(entity.getProcessingReport1());	
		dtoResponse.setProcessingReport2(entity.getProcessingReport2());	
		dtoResponse.setProcessingReport3(entity.getProcessingReport3());	
		dtoResponse.setProcessingReport4(entity.getProcessingReport4());	
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}