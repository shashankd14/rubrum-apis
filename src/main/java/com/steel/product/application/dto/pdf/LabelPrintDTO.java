package com.steel.product.application.dto.pdf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelPrintDTO {

	private String process;

	private Integer inwardEntryId;

	private String partDetailsId;

}
