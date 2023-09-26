package com.steel.product.application.dto.admin;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleSearch {

	@Min(0)
	@Schema(description = "${role.roleid}", example = "1", required = false)
	private int roleId;

	@Size(min = 0, max = 100)
	@Schema(description = "${role.rolename}", example = "Role", required = false)
	private String roleName;

	@Size(max = 1)
	@Schema(description = "${role.activeStatus}", example = "Y", required = false)
	private String activeStatus;

	private int userId;

	@Min(0)
	private int page;

	@Min(0)
	private int size;
}
