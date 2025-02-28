package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_material_file_data")
public class MaterialMasterFiledataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "material_id")
	private Integer materaiId;

	@Column(name = "mm_id")
	private String mmId;

	@Column(name = "mm_description")
	private String mmDescription;

	@Column(name = "category")
	private String category;

	@Column(name = "surfacetype")
	private String surfacetype;

	@Column(name = "subcategory")
	private String subcategory;

	@Column(name = "leafcategory")
	private String leafcategory;

	@Column(name = "form")
	private String form;

	@Column(name = "producttype")
	private String producttype;

	@Column(name = "grade")
	private String grade;

	@Column(name = "subgrade")
	private String subgrade;

	@Column(name = "brand")
	private String brand;

	@Column(name = "diameter")
	private String diameter;

	@Column(name = "thickness")
	private BigDecimal thickness;

	@Column(name = "width")
	private BigDecimal width;

	@Column(name = "length")
	private BigDecimal length;

	@Column(name = "coatingtype")
	private String coatingtype;

	@Column(name = "spangletype")
	private String spangletype;

	@Column(name = "colour")
	private String colour;

	@Column(name = "uom")
	private String uom;

	@Column(name = "hsn")
	private double hsn;

	@Column(name = "tax")
	private double tax;

	@Column(name = "variant_key")
	private double variantKey;

	@Column(name = "filename")
	private String filename;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;
	
	private Integer userId;

}