package com.steel.product.jswone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_po_receive_dtls")
public class POReceiveDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "po_reference")
	private String poReference;

	@Column(name = "warehouse_id")
	private String warehouseId;

	@Column(name = "status")
	private String status;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_on")
	private Date updatedOn;

	private String user_name;

}