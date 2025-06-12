package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.steel.product.application.dto.pricemaster.PriceMasterResponse;
import com.steel.product.application.entity.Process;
import com.steel.product.jswone.entity.GradeMasterJswEntity;
import com.steel.product.jswone.entity.ProductMasterJswEntity;
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

	@Column(name = "location_id")
	private Integer locationId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "process_id")
	private Process process; 
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private ProductMasterJswEntity product;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grade_id")
	private GradeMasterJswEntity grade;

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
		dtoResponse.setGradeId(entity.getGrade().getGradeId() );
		dtoResponse.setLocationId(entity.getLocationId() );
		dtoResponse.setProcessId(entity.getProcess().getProcessId());
		dtoResponse.setProcessName(entity.getProcess().getProcessName());
		dtoResponse.setGradeName(entity.getGrade().getGradeName());
		dtoResponse.setThicknessFrom(entity.getThicknessFrom());
		dtoResponse.setThicknessTo(entity.getThicknessTo());
		dtoResponse.setPrice(entity.getPrice());
		dtoResponse.setProductId( entity.getProduct().getProductId());
		dtoResponse.setProductName(entity.getProduct().getProductName());
		return dtoResponse;
	} 
}