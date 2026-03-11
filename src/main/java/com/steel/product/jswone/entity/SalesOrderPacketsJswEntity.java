package com.steel.product.jswone.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

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
	
	@Column(name = "item_cp_status")
	private String itemCpStatus;

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

	@Column(name = "hsn_or_sac")
	private String hsn_or_sac;

}
