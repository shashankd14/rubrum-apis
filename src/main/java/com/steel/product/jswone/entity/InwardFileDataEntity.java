package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_inward_file_data")
public class InwardFileDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inwarddtlsid")
	private Integer inwarddtlsid;

	@Column(name = "scinwardid")
	private String scinwardid;

	@Column(name = "purchaseinvoiceio")
	private String purchaseinvoiceio;

	@Column(name = "receiveddate")
	private String receiveddate;

	@Column(name = "inwardid")
	private String inwardid;

	@Column(name = "testcertificateno")
	private String testcertificateno;

	@Column(name = "coilno")
	private String coilno;

	@Column(name = "custbatchno")
	private String custbatchno;

	@Column(name = "batchnumber")
	private String batchnumber;

	@Column(name = "presentweight")
	private String presentweight;

	@Column(name = "grossweight")
	private String grossweight;

	@Column(name = "valueofgoods")
	private String valueofgoods;

	@Column(name = "tdcno")
	private String tdcno;

	@Column(name = "vehicleno")
	private String vehicleno;

	@Column(name = "invoicenumber")
	private String invoicenumber;

	@Column(name = "mmid")
	private String mmid;

	@Column(name = "locationname")
	private String locationname;

	@Column(name = "filename")
	private String filename;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	private Integer userId;

}