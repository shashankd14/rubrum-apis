package com.steel.product.application.dto.partDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDetailsPDFResponse {

	private String id;
	private String fileName;
	private String pdfS3Url;
}
