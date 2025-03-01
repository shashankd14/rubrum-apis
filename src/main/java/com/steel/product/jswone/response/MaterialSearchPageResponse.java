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

	private String coatingtype;

	private String spangletype;

	private String colour;

	private String uom;

	private Integer hsn;

	private double tax;

	private Integer variantKey;

}
