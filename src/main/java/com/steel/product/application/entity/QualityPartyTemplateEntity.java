package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;

import lombok.Data;
import lombok.ToString;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "quality_party_template")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QualityPartyTemplateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private QualityTemplateEntity templateEntity=new QualityTemplateEntity();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "npartyid")
	private Party party=new Party();

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

	public static QualityPartyMappingResponse valueOf(QualityPartyTemplateEntity entity) {
		QualityPartyMappingResponse dtoResponse = new QualityPartyMappingResponse();
		dtoResponse.setId( entity.getId());
		dtoResponse.setPartyId(entity.getParty().getnPartyId());
		dtoResponse.setPartyName(entity.getParty().getPartyName() );
		dtoResponse.setTemplateName( entity.getTemplateEntity().getTemplateName());
		dtoResponse.setTemplateId(entity.getTemplateEntity().getTemplateId());
		return dtoResponse;
	}

}