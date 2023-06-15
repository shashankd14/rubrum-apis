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
@Table(name = "received_report_vw")
public class RMReportViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "coil_number")
	private String coilNumber;

	@Column(name = "customer_batch_id")
	private String customerBatchId;

	@Column(name = "received_date")
	private String receivedDate;

	@Column(name = "description")
	private String description;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "cust_inv_no")
	private String custInvNo;

	@Column(name = "cust_inv_date")
	private String custInvDate;

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

	@Column(name = "created_on")
	private String createdOn;
	
	@Column(name = "party_id")
	private int partyId;

}