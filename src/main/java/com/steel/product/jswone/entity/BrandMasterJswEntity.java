package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_brand_master")
public class BrandMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "brand_id")
	private Integer brandId;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "leafcategory_id")
	private Integer leafcategoryId;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on", updatable = false)
	@CreationTimestamp
	private Date updatedOn;

	private Integer updatedBy;
	
	private Integer userId;

}