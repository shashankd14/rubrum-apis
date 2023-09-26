package com.steel.product.application.dto.admin;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RolePermissionList implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@NotNull
	@Schema(description = "${role.operationId}", example = "1", required = true)
	private Integer menuId;
	
	private String permission;

}
