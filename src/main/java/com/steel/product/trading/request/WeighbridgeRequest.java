package com.steel.product.trading.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeighbridgeRequest extends BaseRequest {

	private Integer weighbridgeId;

	private String weighbridgeName;

	private BigDecimal capacityInTons;

	private String address1;

	private String address2;

	private String city;

	private String state;

	private Integer pincode;

	private String contactNo;
}
