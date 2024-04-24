package com.steel.product.trading.request;

import lombok.Data;

@Data
public class SeqGeneratorRequest extends BaseRequest {

	private static final long serialVersionUID = 1L;

	private String fieldName;

}
