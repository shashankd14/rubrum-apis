package com.steel.product.application.entity;

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
@Table(name = "sales_order_child")
public class SalesOrderPacketsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "so_child_id")
	private Integer soChildId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "so_id")
	private SalesOrderEntity soId;

	@Column(name = "instruction_d")
	private Integer instructionId;

	@Column(name = "inward_entry_d")
	private Integer inwardEntryId;

	@Column(name = "coil_no")
	private String coilNo;

	@Column(name = "customer_batch_no")
	private String customerBatchNo;

	@Column(name = "fthickness")
	private BigDecimal fthickness;

	@Column(name = "fwidth")
	private BigDecimal fwidth;

	@Column(name = "flength")
	private BigDecimal flength;

	@Column(name = "fweight")
	private BigDecimal fweight;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")
	private Status status;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "created_on")
	private Date createdOn;

	@UpdateTimestamp
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

}
