package com.steel.product.application.dto.endusertags;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndUserTagsResponse {

	private Integer tagId;

	private String tagName;

	private String createdOn;

	private String updatedOn;
}
