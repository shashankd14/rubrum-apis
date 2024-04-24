package com.steel.product.trading.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "trading_seq_generator")
public class SeqGeneratorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_id")
	private Integer seqId;

	@Column(name = "cur_seq", updatable = false, nullable = false)
	private Integer curSeq;

	@Column(name = "sku_format")
	private String skuFormat;

	@Column(name = "field_name")
	private String fieldName;

}