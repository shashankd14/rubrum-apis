package com.steel.product.application.dto.admin;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RoleRequest {

	private static final long serialVersionUID = 1L;

	private Integer roleId;

	@NotNull
	@NotBlank
	@NotEmpty
	@Schema(description = "${role.rolename}", example = "Role123", required = true)
	private String roleName;

	private String activeStatus;

	private int userId;

	private String roleDesc;

	private List<RolePermissionList> rolePermissionList;

}
