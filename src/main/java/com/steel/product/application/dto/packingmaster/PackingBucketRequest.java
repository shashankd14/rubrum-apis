package com.steel.product.application.dto.packingmaster;

import java.util.List;

import com.steel.product.trading.request.BaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingBucketRequest extends BaseRequest {

	private Integer bucketId;

	private String packingBucketId;

	private List<Integer> packingItemIdList;

	private String packingBucketDesc;

	private int qty;

}
