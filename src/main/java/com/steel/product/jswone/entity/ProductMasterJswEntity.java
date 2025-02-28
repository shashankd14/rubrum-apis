package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_product_master")
public class ProductMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "grade_id")
	private Integer gradeId;

	@Column(name = "subgrade_id")
	private Integer subgradeId;

	@Column(name = "uom_id")
	private Integer uomId;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "surface_id")
	private Integer surfaceId;

	@Column(name = "coatingtype_id")
	private Integer coatingtypeId;

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