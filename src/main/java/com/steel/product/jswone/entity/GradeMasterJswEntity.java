package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_grade_master")
public class GradeMasterJswEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Integer gradeId;

	@Column(name = "grade_name")
	private String gradeName;

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