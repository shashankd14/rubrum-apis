package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_subgrade_master")
public class SubgradeMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subgrade_id")
	private Integer subgradeId;

	@Column(name = "subgrade_name")
	private String subgradeName;

	@Column(name = "grade_id")
	private Integer gradeId;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on", updatable = false)
	@CreationTimestamp
	private Date updatedOn;

	private Integer updatedBy;

	private Integer userId;

}