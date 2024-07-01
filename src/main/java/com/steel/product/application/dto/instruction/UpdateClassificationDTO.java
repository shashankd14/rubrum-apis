package com.steel.product.application.dto.instruction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClassificationDTO {

	private Integer instructionId;

	private Integer inwardId;

	private Integer packetClassificationId;

	private Integer userId;
}
