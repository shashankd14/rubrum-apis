package com.steel.product.jswone.response;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialResponseWithUniqueData {

	private Map<Integer, String> categoryMap = new TreeMap<>();
	private Map<Integer, String> subCategoryMap = new TreeMap<>();
	private Map<Integer, String> leafCategoryMap = new TreeMap<>();
	private Map<Integer, String> brandMap = new TreeMap<>();
	private Map<Integer, String> productMap = new TreeMap<>();
	private Map<Integer, String> uomMap = new TreeMap<>();
	private Map<Integer, String> formMap = new TreeMap<>();
	private Map<Integer, String> gradeMap = new TreeMap<>();
	private Map<Integer, String> subGradeMap = new TreeMap<>();
	private Map<Integer, String> surfaceMap = new TreeMap<>();
	private Map<Integer, String> coatingMap = new TreeMap<>();
	private Map<BigDecimal, BigDecimal> thicknessMap = new TreeMap<>();
	private Map<BigDecimal, BigDecimal> oDiameterMap = new TreeMap<>();
	private Map<BigDecimal, BigDecimal> widthMap = new TreeMap<>();
	private Map<BigDecimal, BigDecimal> iDiameterMap = new TreeMap<>();
	private Map<BigDecimal, BigDecimal> nbMap = new TreeMap<>();

}
