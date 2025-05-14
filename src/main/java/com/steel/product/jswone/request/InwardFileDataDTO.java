package com.steel.product.jswone.request;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class InwardFileDataDTO {

	@CsvBindByPosition(position = 0)
	private String sno;

	@CsvBindByPosition(position = 1)
	private String coilno;

	@CsvBindByPosition(position = 2)
	private String receiveddate;

	@CsvBindByPosition(position = 3)
	private String custbatchno;

	@CsvBindByPosition(position = 4)
	private String batchnumber;

	@CsvBindByPosition(position = 5)
	private String presentweight;

	@CsvBindByPosition(position = 6)
	private String grossweight;

	@CsvBindByPosition(position = 7)
	private String valueofgoods;

	@CsvBindByPosition(position = 8)
	private String tdcno;

	@CsvBindByPosition(position = 9)
	private String vehicleno;

	@CsvBindByPosition(position = 10)
	private String invoicenumber;

	@CsvBindByPosition(position = 11)
	private String mmid;

	@CsvBindByPosition(position = 12)
	private String locationname;
}