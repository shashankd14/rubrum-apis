package com.steel.product.jswone.request;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class InwardFileDataDTO {

	@CsvBindByPosition(position = 0)
	private String sno;

	@CsvBindByPosition(position = 1)
	private String receiveddate;

	@CsvBindByPosition(position = 2)
	private String batchnumber;

	@CsvBindByPosition(position = 3)
	private String presentweight;

	@CsvBindByPosition(position = 4)
	private String grossweight;

	@CsvBindByPosition(position = 5)
	private String valueofgoods;

	@CsvBindByPosition(position = 6)
	private String tdcno;

	@CsvBindByPosition(position = 7)
	private String vehicleno;

	@CsvBindByPosition(position = 8)
	private String mmid;

	@CsvBindByPosition(position = 9)
	private String locationname;

	@CsvBindByPosition(position = 10)
	private String scinwardid;

	@CsvBindByPosition(position = 11)
	private String purchaseinvoiceno;

	@CsvBindByPosition(position = 12)
	private String invoicedate;

	@CsvBindByPosition(position = 13)
	private String ys;

	@CsvBindByPosition(position = 14)
	private String uts;

	@CsvBindByPosition(position = 15)
	private String el;

	@CsvBindByPosition(position = 16)
	private String remarks;

}