package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.steel.product.application.dto.additionalpricemaster.AdditionalPriceMasterResponse;
import lombok.Data;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "addtnl_price_master")
@Data
public class AdditionalPriceMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addtnl_id")
	private Integer id;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "process_id")
	private Integer processId;

	@Column(name = "addtnl_Price_Id")
	private Integer additionalPriceId;

	@Column(name = "range_from")
	private BigDecimal rangeFrom;

	@Column(name = "range_to")
	private BigDecimal rangeTo;

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

	public static AdditionalPriceMasterResponse valueOf(AdditionalPriceMasterEntity entity){
		AdditionalPriceMasterResponse dtoResponse = new AdditionalPriceMasterResponse();
		dtoResponse.setId(entity.getId());
		dtoResponse.setAdditionalPriceId( entity.getAdditionalPriceId() );
		dtoResponse.setPartyId(entity.getPartyId());
		dtoResponse.setProcessId(entity.getProcessId());
		dtoResponse.setRangeFrom(entity.getRangeFrom());
		dtoResponse.setRangeTo(entity.getRangeTo());
		dtoResponse.setPrice(entity.getPrice());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
        return dtoResponse;
    }

}