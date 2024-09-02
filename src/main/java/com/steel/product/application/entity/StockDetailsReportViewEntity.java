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
@Table(name = "stock_details_report_vw")
public class StockDetailsReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "inwardentryid")
	private String inwardentryid;

	@Column(name = "coilnumber")
	private String coilnumber;

	@Column(name = "currentdate")
	private String currentdate;

	@Column(name = "finishingdate")
	private String finishingdate;

	@Column(name = "epaname")
	private String epaname;

	@Column(name = "epalocation")
	private String epalocation;

	@Column(name = "coilage")
	private String coilage;

	@Column(name = "classification_tag")
	private String classificationTag;

	@Column(name = "partyname")
	private String partyname;

	@Column(name = "materialdesc")
	private String materialdesc;

	@Column(name = "parentbatch")
	private String parentbatch;

	@Column(name = "epainputbatch")
	private String epainputbatch;

	@Column(name = "packet_id")
	private String packetId;

	@Column(name = "materialgrade")
	private String materialgrade;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "actualwidth")
	private String actualwidth;

	@Column(name = "actuallength")
	private String actuallength;

	@Column(name = "quality")
	private String quality;

	@Column(name = "netweight")
	private String netweight;

	@Column(name = "qualityremarks")
	private String qualityremarks;

	@Column(name = "clubbedbundleno")
	private String clubbedbundleno;

	@Column(name = "party_id")
	private int partyId;

}