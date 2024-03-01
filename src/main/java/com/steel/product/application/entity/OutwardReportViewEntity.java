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

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "fwidth")
	private String fwidth;

	@Column(name = "flength")
	private String flength;

	@Column(name = "delivery_weight")
	private String deliveryWeight;

	@Column(name = "deliveryid")
	private String deliveryid;

	@Column(name = "createdon")
	private String createdon;

	@Column(name = "vehicleno")
	private String vehicleno;

	@Column(name = "endusertagname")
	private String endusertagname;

	@Column(name = "party_id")
	private int partyId;

}