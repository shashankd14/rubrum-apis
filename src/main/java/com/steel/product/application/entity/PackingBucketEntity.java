package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.packingmaster.PackingBucketChildDTO;
import com.steel.product.application.dto.packingmaster.PackingBucketResponse;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "packing_bucket")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PackingBucketEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bucket_id")
	private Integer bucketId;
	
	@Column(name = "packing_bucket_id")
	private String packingBucketId;

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="bucket_id")
	private Set<PackingBucketChildEntity> itemList = new HashSet<>();
	
	@Column(name = "packing_bucket_desc")
	private String packingBucketDesc;

	@Column(name = "qty")
	private Integer qty;
	
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

	public static PackingBucketResponse valueOf(PackingBucketEntity entity) {
		PackingBucketResponse dtoResponse = new PackingBucketResponse();
		dtoResponse.setBucketId(entity.getBucketId());
		dtoResponse.setPackingBucketId(entity.getPackingBucketId());
		dtoResponse.setPackingBucketDesc(entity.getPackingBucketDesc());
		for(PackingBucketChildEntity childEntity: entity.getItemList()) {
			
			PackingBucketChildDTO resp=new PackingBucketChildDTO();
			resp.setBucketChildId(childEntity.getId());
			resp.setItemId(childEntity.getItemEntity().getItemId());
			resp.setPackingItemId(childEntity.getItemEntity().getPackingItemId());
			resp.setUnit(childEntity.getItemEntity().getUnit());
			resp.setDescription( childEntity.getItemEntity().getDescription() );
			resp.setPackingItemId(childEntity.getItemEntity().getPackingItemId());
			dtoResponse.getItemList().add(resp);
		}
		
		dtoResponse.setQty(entity.getQty());
		dtoResponse.setCreatedBy(entity.getCreatedBy());
		dtoResponse.setUpdatedBy(entity.getUpdatedBy());
		dtoResponse.setCreatedOn(entity.getCreatedOn());
		dtoResponse.setUpdatedOn(entity.getUpdatedOn());
		return dtoResponse;
	}

}