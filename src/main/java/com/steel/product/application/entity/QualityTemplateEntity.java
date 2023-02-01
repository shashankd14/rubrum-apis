package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.QualityTemplateResponse;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "quality_template")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QualityTemplateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_id")
	private Integer templateId;
	
	@Column(name = "templateName")
	private String templateName;
	
	@Column(name = "stageName")
	private String stageName;

	@Column(name = "processId")
	private Integer processId;

	@Column(name = "templateDetails", length = 2000)
	private String templateDetails;
	
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

	public static QualityTemplateResponse valueOf(QualityTemplateEntity entity){
		QualityTemplateResponse dtoResponse = new QualityTemplateResponse();
		dtoResponse.setTemplateId(entity.getTemplateId());
		dtoResponse.setStageName(entity.getStageName());
		dtoResponse.setProcessId( entity.getProcessId() );
		dtoResponse.setTemplateDetails(entity.getTemplateDetails());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}