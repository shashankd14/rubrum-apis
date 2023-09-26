package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;

import lombok.Data;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "price_master")
@Data
public class PriceMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "price_id")
	private Integer id;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "process_id")
	private Integer processId;

	@Column(name = "mat_grade_id")
	private Integer matGradeId;

	@Column(name = "thickness_from")
	private BigDecimal thicknessFrom;

	@Column(name = "thickness_to")
	private BigDecimal thicknessTo;

	@Column(name = "price")
	private BigDecimal price;

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

	public static PriceMasterResponse valueOf(PriceMasterEntity entity){
		PriceMasterResponse dtoResponse = new PriceMasterResponse();
		dtoResponse.setId(entity.getId());
		dtoResponse.setMatGradeId( entity.getMatGradeId() );
		dtoResponse.setPartyId(entity.getPartyId());
		dtoResponse.setProcessId(entity.getProcessId());
		dtoResponse.setThicknessFrom(entity.getThicknessFrom());
		dtoResponse.setThicknessTo(entity.getThicknessTo());
		dtoResponse.setPrice(entity.getPrice());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}