package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.Process;

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "party_id")
	private Party party;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "process_id")
	private Process process;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mat_grade_id")
	private MaterialGrade matGrade;

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

	public static PriceMasterResponse valueOf(PriceMasterEntity entity) {
		PriceMasterResponse dtoResponse = new PriceMasterResponse();
		dtoResponse.setId(entity.getId());
		dtoResponse.setMatGradeId(entity.getMatGrade().getGradeId());
		dtoResponse.setPartyId(entity.getParty().getnPartyId());
		dtoResponse.setProcessId(entity.getProcess().getProcessId());
		dtoResponse.setPartyName(entity.getParty().getPartyName());
		dtoResponse.setProcessName(entity.getProcess().getProcessName());
		dtoResponse.setMatGradeName(entity.getMatGrade().getGradeName());
		dtoResponse.setMaterialDescription( entity.getMatGrade().getParentMaterial().getDescription());
		dtoResponse.setThicknessFrom(entity.getThicknessFrom());
		dtoResponse.setThicknessTo(entity.getThicknessTo());
		dtoResponse.setPrice(entity.getPrice());
		dtoResponse.setMatId( entity.getMatGrade().getParentMaterial().getMatId());
		return dtoResponse;
	}

}