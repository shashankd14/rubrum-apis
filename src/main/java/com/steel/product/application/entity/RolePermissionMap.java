package com.steel.product.application.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ADMIN_ROLE_MENU_MAP")
public class RolePermissionMap implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
	     @JoinColumn(name="role_id", referencedColumnName="role_id")
	})
	private RoleEntity role;
	
	@Column(name = "menu_id")
	private int menuId;

	@Column(name = "op_permission")
	private String permission;
	
	@Column(name = "role_id", insertable = false, updatable = false)
	private int roleId;
	
}
