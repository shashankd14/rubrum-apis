package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class MMIDReceiveData {

	private String mmid;

	private MMIDReceiveProduct product = new MMIDReceiveProduct();
	private MMIDReceiveVariant variant = new MMIDReceiveVariant();
	private MMIDReceiveCategory category = new MMIDReceiveCategory();

}