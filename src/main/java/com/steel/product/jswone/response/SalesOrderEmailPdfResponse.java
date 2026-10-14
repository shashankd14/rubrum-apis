package com.steel.product.jswone.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SalesOrderEmailPdfResponse {

	private Integer soId;
	private String soNumber;
	private String pdfGenerationPart;
	private int totalParties;
	private int sentCount;
	private int failedCount;
	private int skippedCount;
	private String message;
	private List<PartyEmailResult> results = new ArrayList<>();

	@Data
	public static class PartyEmailResult {
		private Integer partyId;
		private String partyName;
		private List<String> emailIds = new ArrayList<>();
		private String fileName;
		/** SENT | FAILED | SKIPPED_NO_EMAIL | SKIPPED_NO_PARTY */
		private String status;
		private String message;
	}
}
