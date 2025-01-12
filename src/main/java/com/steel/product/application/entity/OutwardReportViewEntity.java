package com.steel.product.application.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

	@Column(name = "customername")
	private String customername;

	@Column(name = "aspendcno")
	private String aspendcno;

	@Column(name = "sapinvoiceno")
	private String sapinvoiceno;

	@Column(name = "sapinvoicedate")
	private String sapinvoicedate;

	@Column(name = "materialdesc")
	private String materialdesc;

	@Column(name = "materialgrade")
	private String materialgrade;

	@Column(name = "processname")
	private String processname;

	@Column(name = "qty")
	private String qty;

	@Column(name = "rate")
	private String rate;

	@Column(name = "totalprice")
	private String totalprice;

	@Column(name = "cgst")
	private String cgst;

	@Column(name = "sgst")
	private String sgst;

	@Column(name = "grosstotal")
	private String grosstotal;

	@Column(name = "party_id")
	private int partyId;

}