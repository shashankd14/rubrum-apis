package com.steel.product.jswone.service;

import java.io.File;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import org.springframework.http.ResponseEntity;

public interface SalesOrderPDFJswService {

	File generatePdf(ListPageSearchRequest request) throws IOException, DocumentException;

	/**
	 * Generates a separate PDF per processing-center party and emails it to the
	 * party's email1 (To) / email2 (CC) from product_tblpartydetails.
	 */
	ResponseEntity<Object> emailPdfToParties(ListPageSearchRequest request);

	/**
	 * Generates a separate PDF per processing-center party and returns all of them
	 * base64-encoded in a single response.
	 */
	ResponseEntity<Object> downloadPartyWisePdfs(ListPageSearchRequest request);

}
