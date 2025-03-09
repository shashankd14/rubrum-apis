package com.steel.product.jswone.request;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class MaterialMasterFileDataDTO {

	@CsvBindByPosition(position = 0)
	private String mmId;

	@CsvBindByPosition(position = 1)
	private String mmDescription;

	@CsvBindByPosition(position = 2)
	private String category;

	@CsvBindByPosition(position = 3)
	private String surfacetype;

	@CsvBindByPosition(position = 4)
	private String subcategory;

	@CsvBindByPosition(position = 5)
	private String leafcategory;

	@CsvBindByPosition(position = 6)
	private String form;

	@CsvBindByPosition(position = 7)
	private String producttype;

	@CsvBindByPosition(position = 8)
	private String grade;

	@CsvBindByPosition(position = 9)
	private String subgrade;

	@CsvBindByPosition(position = 10)
	private String brand;

	@CsvBindByPosition(position = 11)
	private String diameter;

	@CsvBindByPosition(position = 12)
	private String thickness;

	@CsvBindByPosition(position = 13)
	private String width;

	@CsvBindByPosition(position = 14)
	private String length;

	@CsvBindByPosition(position = 15)
	private String oDiameter;

	@CsvBindByPosition(position = 16)
	private String nb;

	@CsvBindByPosition(position = 17)
	private String iDiameter;

	@CsvBindByPosition(position = 18)
	private String coatingtype;

	@CsvBindByPosition(position = 19)
	private String spangletype;

	@CsvBindByPosition(position = 20)
	private String colour;

	@CsvBindByPosition(position = 21)
	private String uom;

	@CsvBindByPosition(position = 22)
	private String hsn;

	@CsvBindByPosition(position = 23)
	private String tax;

	@CsvBindByPosition(position = 24)
	private String variantKey;
}