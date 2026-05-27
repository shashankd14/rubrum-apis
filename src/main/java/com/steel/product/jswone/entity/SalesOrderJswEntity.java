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

	@Column(name = "customer_number")
	private String customer_number;

	@Column(name = "customer_name")
	private String customer_name;

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

	@Column(name = "cp_status")
	private String cpStatus;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "branch_id")
	private String branchId;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false)
//	private SalesOrderBranchEntity branch;

	@Column(name = "expected_delivery_date")
	private Date expectedDeliveryDate;

	@Column(name = "cam_code")
	private String camCode;

	@Column(name = "likely_material_date")
	private Date likelyMaterialDate;

	@Column(name = "zbooks_so")
	private String zbooksSo;

	@Column(name = "standard_material_date")
	private Date standardMaterialDate;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "approved_date")
	private Date approvedDate;

	@Column(name = "allocation_date")
	private Date allocationDate;

	@UpdateTimestamp
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@Column (name = "special_delivery_instructions")
	private String special_delivery_instructions;

	@Column (name = "order_confirmation_time")
	private Date order_confirmation_time;

	@OneToMany(mappedBy = "soId", cascade = CascadeType.ALL, orphanRemoval = true)
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
