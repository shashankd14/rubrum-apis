package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "yield_loss_ratio_master")
@Data
public class YieldLossRatioMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ylr_id")
	private Integer ylrId;

	@Column(name = "party_id")
	private Integer partyId;

	@Column(name = "process_id")
	private Integer processId;

	@Column(name = "loss_ratio_percentage_from")
	private BigDecimal lossRatioPercentageFrom;

	@Column(name = "loss_ratio_percentage_to")
	private BigDecimal lossRatioPercentageTo;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;
	
	@Column(name = "comments")
	private String comments;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

}