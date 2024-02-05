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
@Table(name = "processing_report_vw")
public class ProcessingReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "mnth")
	private Integer mnth;
	
	@Column(name = "yer")
	private Integer yer;
	
	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "coilnumber")
	private String coilnumber;

	@Column(name = "customerbatchid")
	private String customerbatchid;
	
	@Column(name = "material_desc")
	private String materialdesc;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "packet_id")
	private Integer packetId;

	@Column(name = "process_name")
	private String processName;

	@Column(name = "processdate")
	private String processdate;

	@Column(name = "packet_thickness")
	private String packetThickness;

	@Column(name = "packet_width")
	private String packetWidth;

	@Column(name = "packet_length")
	private String packetLength;

	@Column(name = "finishing_weight")
	private String finishingWeight;

	@Column(name = "finishing_date")
	private String finishingDate;

	@Column(name = "enduser_tag_name")
	private String enduser_tag_name;

	@Column(name = "party_id")
	private int partyId;

}