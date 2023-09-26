package com.steel.product.application.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminMenuAccessRequest {

	@Schema(description = "${role.userId}", example = "1", required = false)
	private int userId;

}
