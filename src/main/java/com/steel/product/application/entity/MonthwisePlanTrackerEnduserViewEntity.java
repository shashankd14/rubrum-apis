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
@Table(name = "monthwise_plan_tracker_enduserwise")
public class MonthwisePlanTrackerEnduserViewEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "mnth")
	private Integer mnth;
	
	@Column(name = "yer")
	private Integer yer;

	@Column(name = "customerbatchid")
	private String customerbatchid;

	@Column(name = "materialdesc")
	private String materialdesc;

	@Column(name = "materialgrade")
	private String materialgrade;

	@Column(name = "packetid")
	private String packetid;

	@Column(name = "fthickness")
	private String fthickness;

	@Column(name = "plannedwidth")
	private String plannedwidth;

	@Column(name = "plannedlength")
	private String plannedlength;

	@Column(name = "plannedweight")
	private String plannedweight;

	@Column(name = "packetstatus")
	private String packetstatus;

	@Column(name = "endusertagname")
	private String endusertagname;

	@Column(name = "partyid")
	private int partyid;

}