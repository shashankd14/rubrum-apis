package com.steel.product.application.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "outward_report_vw")
public class OutwardReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "mnth")
	private Integer mnth;

	@Column(name = "yer")
	private Integer yer;

	//@Column(name = "customerbatchid")
	//private String customerbatchid;

	//@Column(name = "coilnumber")
	//private String coilnumber;

	@Column(name = "vehicleno")
	private String vehicleno;

	@Column(name = "customername")
	private String customername;

	@Column(name = "aspendcno")
	private int aspendcno;

	@Column(name = "sapinvoiceno")
	private String sapinvoiceno;

	@Column(name = "sapinvoicedate")
	private String sapinvoicedate;

	@Column(name = "materialdesc")
	private String materialdesc;

	//@Column(name = "materialgrade")
	//private String materialgrade;

	@Column(name = "processname")
	private String processname;

	@Column(name = "tdc_no")
	private String tdcNo;

	@Column(name = "qty")
	private BigDecimal qty;

	//@Column(name = "base_proce")
	//private BigDecimal basePrice;

	//@Column(name = "packing_charges")
	//private BigDecimal packingCharges;

	//@Column(name = "lamination_charges")
	//private BigDecimal laminationCharges;

	//@Column(name = "additional_charges")
	//private BigDecimal additionalCharges;

	@Column(name = "rate")
	private BigDecimal rate;

	@Column(name = "cgst")
	private BigDecimal cgst;

	@Column(name = "sgst")
	private BigDecimal sgst;

	@Column(name = "totalprice")
	private BigDecimal totalprice;

	@Column(name = "gross_total")
	private BigDecimal grossTotal;

	@Column(name = "party_id")
	private int partyId;

}