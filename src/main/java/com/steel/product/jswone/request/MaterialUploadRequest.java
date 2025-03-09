package com.steel.product.jswone.request;

import com.steel.product.trading.request.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialUploadRequest extends BaseRequest {

	private boolean uploadFlag; 

}
