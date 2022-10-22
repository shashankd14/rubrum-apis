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
@Table(name = "wip_report_vw")
public class WIPReportViewEntity {

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

	@Column(name = "net_weight")
	private String netWeight;

	@Column(name = "in_stock_weight")
	private String inStockWeight;

	@Column(name = "wip_weight")
	private String wipWeight;

	@Column(name = "thickness")
	private String thickness;

	@Column(name = "planned_width")
	private String plannedWidth;

	@Column(name = "planned_length")
	private String plannedLength;

	@Column(name = "planned_weight")
	private String plannedWeight;

	@Column(name = "inward_status")
	private String inwardStatus;

	@Column(name = "partyId")
	private int partyId;

}