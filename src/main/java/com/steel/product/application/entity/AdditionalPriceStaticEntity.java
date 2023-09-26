package com.steel.product.application.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "addtnl_price_static_dtls")
@Data
public class AdditionalPriceStaticEntity {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "process_id")
	private Integer processId;

	@Column(name = "price_desc")
	private String priceDesc;

}