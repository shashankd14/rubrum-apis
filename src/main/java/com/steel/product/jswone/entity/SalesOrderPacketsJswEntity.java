package com.steel.product.jswone.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "jsw_sales_order_child")
public class SalesOrderPacketsJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "so_child_id")
	private Integer soChildId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "so_id")
	private SalesOrderJswEntity soId;

	@Column(name = "mm_id")
	private String mmId;
	
	@Column(name = "item_id")
	private String item_id;
	
	@Column(name = "line_item_id")
	private String lineItemId;
	
	@Column(name = "soqty")
	private BigDecimal soqty;
	
	@Column(name = "allocated_soqty")
	private BigDecimal allocatedSoqty;

	@Column(name = "allocated_stts")
	private String allocatedStts;
	
	@Column(name = "special_instructions")
	private String specialInstructions;
	
	@Column(name = "material_name")
	private String materialName;
	
	@Column(name = "item_so_status")
	private String itemSoStatus;
	
	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false, updatable = false)
	private Date createdOn;

	@Column(name = "allocation_date", nullable = false, updatable = false)
	private Date allocationDate;

	@Column(name = "allocation_by")
	private Integer allocationBy;

	@Column(name = "approved_date", nullable = false, updatable = false)
	private Date approvedDate;

	@UpdateTimestamp
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@Column(name = "wearhouse_id")
	private String wearhouseId;

	@Column(name = "tax_percentage")
	private String tax_percentage;

	@Column(name = "quantity_invoiced")
	private BigDecimal quantity_invoiced;

	@Column(name = "hsn_or_sac")
	private String hsn_or_sac;

	@Column(name = "number_of_sheets")
	private String number_of_sheets;
}
