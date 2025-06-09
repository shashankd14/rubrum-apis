package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
@Table(name = "jsw_sales_order")
public class SalesOrderJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "so_id")
	private Integer soId;

	@Column(name = "so_number")
	private String soNumber;

	@Column(name = "socreatedate")
	private Date socreatedate;

	@Column(name = "deliverymethod")
	private String deliverymethod;

	@Column(name = "destinationcode")
	private String destinationcode;

	@Column(name = "refno")
	private String refno;

	@Column(name = "joplsorefno")
	private String joplsorefno;

	@Column(name = "bizsegment")
	private String bizsegment;

	@Column(name = "ecommerce")
	private String ecommerce;

	@Column(name = "supplysource")
	private String supplysource;

	@Column(name = "typeofsupply")
	private String typeofsupply;

	@Column(name = "incomingpayment")
	private String incomingpayment;

	@Column(name = "paymentmode")
	private String paymentmode;

	@Column(name = "terms")
	private String terms;

	@Column(name = "customerid")
	private String customerid;

	@Column(name = "total_soqty")
	private BigDecimal totalSoqty;

	@Column(name = "total_allocated_soqty")
	private BigDecimal totalAllocatedSoqty;

	@Column(name = "allocated_stts")
	private String allocatedStts;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "so_status")
	private String soStatus;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false, updatable = false)
	private Date createdOn;

	@Column(name = "approved_date", nullable = false, updatable = false)
	private Date approvedDate;

	@Column(name = "allocation_date", nullable = false, updatable = false)
	private Date allocationDate;

	@UpdateTimestamp
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@OneToMany(mappedBy = "soId", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private Set<SalesOrderPacketsJswEntity> itemslist;

	public void removeItem(SalesOrderPacketsJswEntity instruction) {
		this.getItemslist().remove(instruction);
		instruction.setSoId(null);
	}

	public void addItem(SalesOrderPacketsJswEntity instruction) {
		if (this.itemslist == null) {
			this.itemslist = new LinkedHashSet<>();
		}
		this.getItemslist().add(instruction);
		instruction.setSoId(this);
	}

}
