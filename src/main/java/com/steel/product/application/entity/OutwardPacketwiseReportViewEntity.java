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
@Table(name = "outward_packetwise_report_vw")
public class OutwardPacketwiseReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "planid")
	private int planid;

	@Column(name = "mnth")
	private int mnth;

	@Column(name = "yer")
	private int yer;

	@Column(name = "coilnumber")
	private String coilnumber;

	@Column(name = "customerbatchid")
	private String customerbatchid;

	@Column(name = "locationname")
	private String locationname;

	@Column(name = "materialdesc")
	private String materialdesc;

	@Column(name = "materialgrade")
	private String materialgrade;

	@Column(name = "subgrade")
	private String subgrade;
	
	@Column(name = "remarks")
	private String remarks;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "fwidth")
	private String fwidth;

	@Column(name = "flength")
	private String flength;

	@Column(name = "delivery_weight")
	private String deliveryWeight;

	@Column(name = "additional_weight")
	private String additionalWeight;

	@Column(name = "deliveryid")
	private String deliveryid;

	@Column(name = "createdon")
	private String createdon;

	@Column(name = "noofpieces")
	private String noofpieces;

	@Column(name = "vehicleno")
	private String vehicleno;

	@Column(name = "party_id")
	private int partyId;

}