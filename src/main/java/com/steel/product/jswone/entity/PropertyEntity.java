package com.steel.product.jswone.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "property_table")
public class PropertyEntity {

	@Id
	@Column(name = "property_id")
	private int propertyId;

	@Column(name = "property_name")
	private String propertyName;

	@Column(name = "property_value")
	private String propertyValue;

}