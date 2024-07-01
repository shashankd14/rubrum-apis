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
@Table(name = "stock_summary_report_vw")
public class StockSummaryReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "coilnumber")
	private String coilNumber;

	@Column(name = "customerbatchid")
	private String customerBatchId;

	@Column(name = "materialdesc")
	private String materialDesc;

	@Column(name = "materialgrade")
	private String materialGrade;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "fwidth")
	private String fwidth;

	@Column(name = "flength")
	private String flength;

	@Column(name = "netweight")
	private String netweight;

	@Column(name = "instockweight")
	private String instockweight;

	@Column(name = "fgqty")
	private String fgqty;

	@Column(name = "fgclassification")
	private String fgclassification;
	
	@Column(name = "edgetrimclassification")
	private String edgetrimclassification;

	@Column(name = "qualitydefects")
	private String qualitydefects;
	
	@Column(name = "cutendsclassification")
	private String cutendsclassification;
	
	@Column(name = "othersclassification")
	private String othersclassification;
	
	@Column(name = "wipclassification")
	private String wipclassification;
	
	@Column(name = "blankclassification")
	private String blankclassification;

	@Column(name = "unprocessedweight")
	private String unprocessedweight;
	
	@Column(name = "wipqty")
	private String wipqty;
	
	@Column(name = "dispatchedweight")
	private String dispatchedweight;

	@Column(name = "inwardstatus")
	private String inwardstatus;
	
	@Column(name = "remarks")
	private String remarks;

	@Column(name = "partyid")
	private int partyId;

}