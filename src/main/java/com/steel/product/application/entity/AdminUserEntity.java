package com.steel.product.application.entity;

import lombok.Data;

import java.util.ArrayList;
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
@Table(name = "admin_user")
public class AdminUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_name", length = 150)
	private String userName;

	@Column(name = "first_name", length = 150)
	private String firstName;

	@Column(name = "last_name", length = 150)
	private String lastName;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "raw_password", length = 100)
	private String rawPassword;

	@Column(name = "email_id", length = 100)
	private String emailId;

	@Column(name = "enabled")
	private short enabled;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "user_data_visible")
	private String userDataVisible;

	@Column(name = "fail_lgn_counter")
	private int failLgnCounter;

	@Column(name = "current_login_time")
	private Date currentLoginTime;

	@Column(name = "last_login_time")
	private Date lastLoginTime;

	@OneToMany(mappedBy = "userEntityid", cascade = CascadeType.ALL)
	private List<UserRoleMap> userRoleMap = new ArrayList<>();

	@OneToMany(mappedBy = "userEntityid", cascade = CascadeType.ALL)
	private List<UserPartyMap> userPartyMap = new ArrayList<>();

}
