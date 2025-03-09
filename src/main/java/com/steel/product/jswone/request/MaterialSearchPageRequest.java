package com.steel.product.jswone.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSearchPageRequest {

	private Integer pageNo;

	private Integer pageSize;

	private BigDecimal length;

	private BigDecimal width;

	private BigDecimal thickness;

	private BigDecimal nb;

	private BigDecimal oDiameter;

	private BigDecimal iDiameter;
	
	private String mmid;

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

}
