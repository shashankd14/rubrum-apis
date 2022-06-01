package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.entity.InwardEntry;

import lombok.Data;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userid")
	private int userId;

	@Column(name = "user_name", length = 50)
	private String userName;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "email_id", length = 100)
	private String email;

	@Column(name = "role_id", length = 50)
	private String role;

	@Column(name = "enabled")
	private short enabled;
	
	@Column(name = "fail_lgn_counter")
	private int failLgnCounter;
	
	@Column(name = "current_login_time")
	private Date currentLoginTime;

	@Column(name = "last_login_time")
	private Date lastLoginTime;
	
	@JsonBackReference
	@OneToMany(mappedBy = "createdBy", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JsonIgnoreProperties({ "createdBy" })
	private List<InwardEntry> inwardEntry;

	@JsonBackReference
	@OneToMany(mappedBy = "updatedBy", cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JsonIgnoreProperties({ "updatedBy" })
	private List<InwardEntry> inwardEntry1;

}
