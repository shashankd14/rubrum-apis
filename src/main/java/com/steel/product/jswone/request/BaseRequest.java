package com.steel.product.jswone.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseRequest {

	private Integer userId;

	private String ipAddress;

	private String requestId;

}
