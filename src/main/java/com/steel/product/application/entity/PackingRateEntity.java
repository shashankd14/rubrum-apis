package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "packing_rate_master")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PackingRateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "packing_rate_id")
	private Integer packingRateId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bucket_id")
	private PackingBucketEntity bucketEntity=new PackingBucketEntity();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "npartyid")
	private Party party=new Party();

	@Column(name = "rate")
	private BigDecimal packingRate;
	
	@Column(name = "packing_rate_desc")
	private String packingRateDesc;

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

	public static PackingRateMasterResponse valueOf(PackingRateEntity entity) {
		PackingRateMasterResponse dtoResponse = new PackingRateMasterResponse();
		dtoResponse.setPackingRateId(entity.getPackingRateId());
		dtoResponse.setPackingBucketId(entity.getBucketEntity().getBucketId());
		dtoResponse.setPackingBucketName( entity.getBucketEntity().getPackingBucketId() );
		dtoResponse.setPartyId( entity.getParty().getnPartyId());
		dtoResponse.setPartyName( entity.getParty().getPartyName());
		dtoResponse.setPackingRate( entity.getPackingRate());
		dtoResponse.setPackingRateDesc( entity.getPackingRateDesc() );
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
		return dtoResponse;
	}

}