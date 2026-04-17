package com.steel.product.jswone.service;

import java.io.File;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.quality.ListPageSearchRequest;

public interface SalesOrderPDFJswService {
	
	File generatePdf(ListPageSearchRequest request) throws IOException, DocumentException;



}
