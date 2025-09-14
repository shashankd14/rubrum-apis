package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "jsw_warehouse_location_map")
public class WarehouseLocationMappingJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "warehouse_id")
	private String warehouse_id;

	@Column(name = "location_id")
	private int location_id;

	@Column(name = "user_id")
	private int user_id;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on", updatable = false)
	@CreationTimestamp
	private Date updatedOn;

}