package com.steel.product.jswone.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSearchPageResponse {

	private Integer materaiId;

	private String mmId;

	private String mmDescription;

	private int categoryId;

	private int subcategoryId;

	private int leafcategoryId;

	private int formId;

	private int producttypeId;

	private int gradeId;

	private int subgradeId;

	private int brandId;

	private int surfacetypeId;

	private int coatingtypeId;

	private int uomId;

	private String category;

	private String surfacetype;

	private String subcategory;

	private String leafcategory;

	private String form;

	private String producttype;

	private String grade;

	private String subgrade;

	private String brand;

	private String diameter;

	private BigDecimal thickness;

	private BigDecimal width;

	private BigDecimal length;

	private BigDecimal oDiameter;

	private BigDecimal nb;

	private BigDecimal iDiameter;

	private String coatingtype;

	private String colour;

	private String uom;

	private String hsn;

	private String tax;

	private String variantKey;

}
