package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GradeDTO {

	private int gradeId;

	private String gradeName;

	List<SubGradeDTO> subgradesList = new ArrayList<>();

}