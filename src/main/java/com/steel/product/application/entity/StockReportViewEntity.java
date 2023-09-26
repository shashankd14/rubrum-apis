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
@Table(name = "stock_report_vw")
public class StockReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "coilnumber")
	private String coilNumber;

	@Column(name = "customerbatchid")
	private String customerBatchId;

	@Column(name = "material_desc")
	private String materialDesc;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "fwidth")
	private String fwidth;

	@Column(name = "flength")
	private String flength;

	@Column(name = "netWeight")
	private String netWeight;

	@Column(name = "inward_status")
	private String inwardStatus;

	@Column(name = "in_stock_weight")
	private String inStockWeight;

	@Column(name = "Unprocessed_Weight")
	private String unProcessedWeight;

	@Column(name = "partyId")
	private int partyId;

}