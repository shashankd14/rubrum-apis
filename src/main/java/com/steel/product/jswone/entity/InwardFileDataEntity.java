package com.steel.product.jswone.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_inward_file_data")
public class InwardFileDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inwarddtlsid")
	private Integer inwarddtlsid;

	@Column(name = "receiveddate")
	private String receiveddate;

	@Column(name = "batchnumber")
	private String batchnumber;

	@Column(name = "coilnumber")
	private String coilnumber;

	@Column(name = "presentweight")
	private String presentweight;

	@Column(name = "grossweight")
	private String grossweight;

	@Column(name = "valueofgoods")
	private BigDecimal valueofgoods;

	@Column(name = "tdcno")
	private String tdcno;

	@Column(name = "vehicleno")
	private String vehicleno;	

	@Column(name = "mmid")
	private String mmid;

	@Column(name = "locationname")
	private String locationname;

	@Column(name = "scinwardid")
	private String scinwardid;

	@Column(name = "purchaseinvoiceno")
	private String purchaseinvoiceno;

	@Column(name = "invoicedate")
	private String invoicedate;

	@Column(name = "ys")
	private String ys;

	@Column(name = "uts")
	private String uts;

	@Column(name = "el")
	private String el;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "filename")
	private String filename;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

}