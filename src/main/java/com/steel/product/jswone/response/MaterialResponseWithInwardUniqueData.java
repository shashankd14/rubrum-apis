package com.steel.product.jswone.response;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialResponseWithInwardUniqueData {

	private Map<Integer, String> productMap = new TreeMap<>();
	private Map<Integer, String> gradeMap = new TreeMap<>();
	private Map<Integer, String> subGradeMap = new TreeMap<>();
	private Map<Integer, String> brandMap = new TreeMap<>();
}
