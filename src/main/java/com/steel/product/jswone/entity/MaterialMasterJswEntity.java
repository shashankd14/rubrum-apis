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
@Table(name = "jsw_material_master")
public class MaterialMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "material_id")
	private Integer materialId;

	@Column(name = "mm_id")
	private String mmId;

	@Column(name = "mm_description")
	private String mmDescription;

	@Column(name = "category_id")
	private Integer categoryId;

	@Column(name = "subcategory_id")
	private Integer subcategoryId;

	@Column(name = "leafcategory_id")
	private Integer leafcategoryId;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "producttype_id")
	private Integer producttypeId;

	@Column(name = "grade_id")
	private Integer gradeId;

	@Column(name = "subgrade_id")
	private Integer subgradeId;

	@Column(name = "brand_id")
	private Integer brandId;

	@Column(name = "diameter")
	private String diameter;

	@Column(name = "thickness")
	private BigDecimal thickness;

	@Column(name = "width")
	private BigDecimal width;

	@Column(name = "lngth")
	private BigDecimal length;

	@Column(name = "o_diameter")
	private BigDecimal oDiameter;

	@Column(name = "nb")
	private BigDecimal nb;

	@Column(name = "i_diameter")
	private BigDecimal iDiameter;

	@Column(name = "surfacetype_id")
	private Integer surfacetypeId;

	@Column(name = "coatingtype_id")
	private Integer coatingtypeId;

	@Column(name = "spangle_type")
	private String spangleType;

	@Column(name = "colour")
	private String colour;

	@Column(name = "uom_id")
	private Integer uomId;

	@Column(name = "hsn")
	private String hsn;

	@Column(name = "tax")
	private String tax;

	@Column(name = "variant_key")
	private String variantKey;

	@Column(name = "filename")
	private String filename;

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