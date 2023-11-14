package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.lamination.LaminationChargesResponse;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "lamination_charges")
@Data
@ToString
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LaminationChargesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lamination_id")
	private Integer laminationId;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "lamination_details_id")
	private Integer laminationDetailsId;

	@Column(name = "charges")
	private BigDecimal charges;

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

	public static LaminationChargesResponse valueOf(LaminationChargesEntity entity) {
		LaminationChargesResponse dtoResponse = new LaminationChargesResponse();
		dtoResponse.setLaminationId(entity.getLaminationId());
		dtoResponse.setPartyId(entity.getPartyId());
		dtoResponse.setLaminationDetailsId(entity.getLaminationDetailsId());
		dtoResponse.setCharges( entity.getCharges());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
		return dtoResponse;
	}

}