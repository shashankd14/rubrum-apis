package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseRequest {

	private Integer userId;

	private String ipAddress;

	private String requestId;

}
