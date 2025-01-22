package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
@Table(name = "sales_order")
public class SalesOrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "so_id")
	private Integer soId;

	@Column(name = "so_number")
	private String soNumber;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "total_weight")
	private BigDecimal totalWeight;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")
	private Status status;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@CreationTimestamp
	@Column(name = "createdOn", nullable = false, updatable = false)
	private Date createdOn;

	@UpdateTimestamp
	@Column(name = "updatedOn")
	private Date updatedOn;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@OneToMany(mappedBy = "soId", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private Set<SalesOrderPacketsEntity> instructions;

	public void addInstruction(SalesOrderPacketsEntity instruction) {
		if (this.instructions == null) {
			this.instructions = new LinkedHashSet<>();
		}
		this.getInstructions().add(instruction);
		instruction.setSoId(this);
	}

	public void removeInstruction(SalesOrderPacketsEntity instruction) {
		this.getInstructions().remove(instruction);
		instruction.setSoId(null);
	}

}
