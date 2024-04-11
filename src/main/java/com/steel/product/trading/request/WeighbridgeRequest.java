package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeighbridgeRequest extends BaseRequest {

	private Integer weighbridgeId;

	private String weighbridgeName;

	private String address1;

	private String address2;

	private String city;

	private String state;

	private Integer pincode;
}
