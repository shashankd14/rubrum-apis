package com.steel.product.application.dto.admin;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.entity.RolePermissionMap;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleEntityDto {

	private int roleId;

	private String roleName;

	private String roleDesc;

	private String activeStatus;

	private String modifyBy;

	private Date modifyDate;

	private List<RolePermissionMap> permissions;

}
