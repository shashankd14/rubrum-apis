package com.steel.product.trading.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "trading_inward_items")
@Data
public class InwardTradingChildEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "itemchild_id")
	private Integer itemchildId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inwardid")
	private InwardTradingEntity inwardId;

	@Column(name = "inward_item_id")
	private String inwardItemId;

	@Column(name = "item_id")
	private Integer itemId;

	@Column(name = "unit")
	private String unit;

	@Column(name = "unit_volume")
	private Integer unitVolume;

	@Column(name = "net_weight")
	private BigDecimal netWeight;

	@Column(name = "rate")
	private BigDecimal rate;

	@Column(name = "volume")
	private Integer volume;

	@Column(name = "actual_noof_pieces")
	private Integer actualNoofPieces;

	@Column(name = "theoretical_weight")
	private BigDecimal theoreticalWeight;

	@Column(name = "weight_variance")
	private BigDecimal weightVariance;

	@Column(name = "theoretical_noof_pieces")
	private Integer theoreticalNoofPieces;
	
	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;
	
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