package com.steel.product.jswone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_powise_mmid_details")
public class POWiseMmidDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "po_reference")
	private String poReference;

	@Column(name = "mm_id")
	private String mmId;

	@Column(name = "po_id")
	private String poId;

	@Column(name = "mmid_details_object")
	private String mmidDetailsObject;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_on")
	private Date updatedOn;

}