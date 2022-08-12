package com.steel.product.application.dto.packingmaster;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackingBucketRequest {

	private Integer bucketId;

	private String packingBucketId;

	private List<Integer> packingItemIdList;

	private String packingBucketDesc;

	private int qty;

}
