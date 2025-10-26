package com.steel.product.jswone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_so_receive_dtls")
public class SOReceiveDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "so_no")
	private String soNo;

	@Column(name = "warehouse_id")
	private String warehouseId;

	@Column(name = "so_id")
	private String soId;

	@Column(name = "so_status")
	private String soStatus;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_on")
	private Date updatedOn;

	private String user_name;

}