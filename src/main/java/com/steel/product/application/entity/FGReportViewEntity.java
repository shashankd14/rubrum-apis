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
@Table(name = "fg_report_vw")
public class FGReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "coilnumber")
	private String coilNumber;

	@Column(name = "customerbatchid")
	private String customerBatchId;

	@Column(name = "finishing_date")
	private String finishingDate;

	@Column(name = "material_desc")
	private String materialDesc;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "packet_id")
	private String packetId;

	@Column(name = "thickness")
	private String thickness;
	
	@Column(name = "actualwidth")
	private String actualwidth;
	
	@Column(name = "actuallength")
	private String actuallength;
	
	@Column(name = "actualweight")
	private String actualweight;

	@Column(name = "classification_tag")
	private String classificationTag;

	@Column(name = "enduser_tag_name")
	private String enduserTagName;

	@Column(name = "partyId")
	private int partyId;

}