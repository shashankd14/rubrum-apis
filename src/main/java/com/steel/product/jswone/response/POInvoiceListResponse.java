package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class POInvoiceListResponse {

	private String poInvoiceNo;

	private String poInvSyncStatus;

	private String manualPoFlag;

	private String poInvSyncRemarks;

	private String billId;

	private String zohoDocumentUploadStts;

	private String zohoDocumentUploadRemarks;
	
	private List<POInvoiceListChildResponse> coilList = new ArrayList<>();

}
