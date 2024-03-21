package com.steel.product.trading.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequest extends BaseRequest {

	private List<Integer> ids;

}
