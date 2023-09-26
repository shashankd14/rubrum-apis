package com.steel.product.application.dto.packingmaster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class PackingBucketResponse {

	private Integer bucketId;

	private String packingBucketId;

	private List<PackingBucketChildDTO> itemList = new ArrayList<>();
	
	private String packingBucketDesc;

	private int qty;

	private Integer createdBy;

	private Integer updatedBy;

	private Date createdOn;

	private Date updatedOn;

}
