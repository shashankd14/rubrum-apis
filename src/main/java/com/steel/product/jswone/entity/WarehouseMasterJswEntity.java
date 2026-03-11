package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "jsw_warehouse_master")
public class WarehouseMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "ware_house_name")
	private String ware_house_name;

	@Column(name = "party_id")
	private int partyId;

	@Column(name = "ware_house_id")
	private String wareHouseId;

	@Column(name = "branch_id")
	private String branchId;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on", updatable = false)
	@CreationTimestamp
	private Date updatedOn;

}