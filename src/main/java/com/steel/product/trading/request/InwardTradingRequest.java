package com.steel.product.trading.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InwardTradingRequest extends BaseRequest {

	private Integer vendorId;

	private String vendorName;

	private String vendorNickName;

	private String phoneNo;

	private String contactName;

	private String contactNo;

	private String emailId;

	private String panNumber;

	private String tanNumber;

	private String gstNumber;

	private String processTags;

	// Address

	private String address1;

	private String address2;

	private String city;

	private String state;

	private Integer pincode;

	// Alternate Address

	private String alternateAddress1;

	private String alternateAddress2;

	private String alternateCity;

	private String alternateState;

	private Integer alternatePincode;

	private Boolean isDeleted;

}
