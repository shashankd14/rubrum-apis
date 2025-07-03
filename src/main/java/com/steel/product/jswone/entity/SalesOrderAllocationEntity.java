package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
@Table(name = "jsw_sales_order_allocation")
public class SalesOrderAllocationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "so_allocation_id")
	private Integer so_allocation_id;

	@Column(name = "so_child_id")
	private Integer soChildId;

	@JoinColumn(name = "so_id")
	private Integer soId;

	@Column(name = "allocated_soqty")
	private BigDecimal allocatedSoqty;

	@Column(name = "allocated_stts")
	private String allocatedStts;

	@Column(name = "instruction_id")
	private Integer instructionId;

	@Column(name = "inward_entry_d")
	private Integer inwardEntryId;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "allocation_date", nullable = false, updatable = false)
	private Date allocationDate;

	@Column(name = "allocation_by")
	private Integer allocationBy;

	@UpdateTimestamp
	@Column(name = "updated_on")
	private Date updatedOn;
}
