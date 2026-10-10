package com.steel.product.jswone.controller;

import com.steel.product.jswone.request.JSWReportRequest;
import com.steel.product.jswone.service.JSWReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JSWReportController {

	private final JSWReportService jSWReportService;

	public JSWReportController(JSWReportService jSWReportService) {
		this.jSWReportService = jSWReportService;
	}

	/**
	 * POST /reports/download   (old path /reports/packetlevel_audit still works)
	 *
	 * Binary attachment. Preferable for a plain browser download since it avoids
	 * the ~33% base64 overhead entirely.
	 *
	 * Body:
	 * {
	 *   "reportType": "PURCHASE | SALES | STOCK | OPEN_ORDERS | JOB_ORDERS | FIFO_BREACH | AUDIT_TRAIL",
	 *   "fromDate":   "2026-09-01",      // inclusive, ignored for STOCK
	 *   "toDate":     "2026-09-30",      // inclusive, ignored for STOCK
	 *   "locationId": 0
	 * }
	 * reportType blank -> AUDIT_TRAIL. Dates blank -> 1st of current month .. today.
	 */
	@PostMapping({"/reports/download"})
	public ResponseEntity<byte[]> exportDownload(@RequestBody(required = false) JSWReportRequest request) {

		JSWReportService.ExportFile file =
				jSWReportService.export(request != null ? request : new JSWReportRequest());
		byte[] bytes = file.getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(JSWReportService.XLSX_CONTENT_TYPE));
		headers.setContentDispositionFormData("attachment", file.getFileName());
		headers.setContentLength(bytes.length);
		// Exposed so a browser fetch() can read the filename back.
		headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		headers.setCacheControl("no-store");
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	
	
}
