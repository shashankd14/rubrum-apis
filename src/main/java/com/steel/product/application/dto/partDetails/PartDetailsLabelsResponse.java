package com.steel.product.application.dto.partDetails;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDetailsLabelsResponse {

	private String id;
	private String fileName;
	private String labelUrl;
	private Date modifiedTime;
}
