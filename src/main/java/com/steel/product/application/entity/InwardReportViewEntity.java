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
@Table(name = "inward_report_vw")
public class InwardReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "testcertificatenumber")
	private String testcertificatenumber;

	@Column(name = "coilnumber")
	private String coilnumber;

	@Column(name = "customerbatchid")
	private String customerbatchid;

	@Column(name = "received_date")
	private String receivedDate;

	@Column(name = "material_desc")
	private String materialdesc;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "subgrade")
	private String subgrade;

	@Column(name = "locationname")
	private String locationname;

	@Column(name = "mm_id")
	private String mmId;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "fwidth")
	private String fwidth;

	@Column(name = "flength")
	private String flength;

	@Column(name = "net_weight")
	private String netWeight;

	@Column(name = "inward_status")
	private String inwardStatus;

	@Column(name = "customerinvoiceno")
	private String customerinvoiceno;

	@Column(name = "createdon")
	private String createdon;

	@Column(name = "customerinvoicedate")
	private String customerinvoicedate;

	@Column(name = "vehicleno")
	private String vehicleno;

	@Column(name = "valueofgoods")
	private String valueofgoods;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "party_id")
	private int partyId;

}