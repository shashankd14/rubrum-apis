package com.steel.product.jswone.request;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class InwardFileDataDTO {

	@CsvBindByPosition(position = 0)
	private String scinwardid;

	@CsvBindByPosition(position = 1)
	private String purchaseinvoiceio;

	@CsvBindByPosition(position = 2)
	private String receiveddate;

	@CsvBindByPosition(position = 3)
	private String inwardid;

	@CsvBindByPosition(position = 4)
	private String testcertificateno;

	@CsvBindByPosition(position = 5)
	private String coilno;

	@CsvBindByPosition(position = 6)
	private String custbatchno;

	@CsvBindByPosition(position = 7)
	private String batchnumber;

	@CsvBindByPosition(position = 8)
	private String presentweight;

	@CsvBindByPosition(position = 9)
	private String grossweight;

	@CsvBindByPosition(position = 10)
	private String valueofgoods;

	@CsvBindByPosition(position = 11)
	private String tdcno;

	@CsvBindByPosition(position = 12)
	private String vehicleno;

	@CsvBindByPosition(position = 13)
	private String invoicenumber;

	@CsvBindByPosition(position = 14)
	private String mmid;

	@CsvBindByPosition(position = 15)
	private String locationname;
}