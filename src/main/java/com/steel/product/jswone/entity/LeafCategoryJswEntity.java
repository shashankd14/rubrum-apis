package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_leafcategory_master")
public class LeafCategoryJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "leafcategory_id")
	private Integer leafcategoryId;

	@Column(name = "leafcategory_name")
	private String leafcategoryName;

	@Column(name = "subcategory_id")
	private Integer subcategoryId;

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