package com.steel.product.jswone.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.steel.product.application.dto.quality.ListPageSearchRequest;
import com.steel.product.application.dto.quality.QIRTemplateDtlsJsonArrayDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListDTO;
import com.steel.product.application.dto.salesorder.SalesOrderListResponse;
import com.steel.product.jswone.repository.SalesOrderAllocationJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesOrderPDFServiceJswImpl implements SalesOrderPDFJswService {

	/** Key used when the warehouse has no party mapped. */
	private static final Integer NO_PARTY_KEY = 0;

	@Value("${imagelogopath}")
	private String imagelogopath;

	@Value("${jswone.so.mail.from:${spring.mail.username:}}")
	private String mailFrom;

	/** Optional comma separated CC list, e.g. ops@jswone.in */
	@Value("${jswone.so.mail.cc:}")
	private String mailCc;

	/**
	 * Non-prod safety net: when set, every SO PDF mail goes to these addresses instead of
	 * the party, and the subject is tagged with the intended recipients. Leave blank in prod.
	 */
	@Value("${jswone.so.mail.override-to:}")
	private String mailOverrideTo;

	@Autowired
	private SalesOrderJswRepository salesOrderRepository;

	@Autowired
	private SalesOrderAllocationJswRepository soAllocationRepo;

	@Autowired
	private JavaMailSender mailSender;

	// =====================================================================
	// 1. Download PDF (existing behaviour: one PDF, one page per party)
	// =====================================================================

	@Override
	public File generatePdf(ListPageSearchRequest request) {
		File file = null;
		try {
			file = generateSOPdf(request);
		} catch (Exception e) {
			log.error("PDF generation failed for soId=" + request.getSoId(), e);
		}
		return file;
	}

	private File generateSOPdf(ListPageSearchRequest request) throws IOException {

		Map<Integer, SalesOrderListResponse> partyWise = soDetailsBySoId(request);
		log.info("party-wise map size == " + partyWise.size());

		File file = File.createTempFile("soAllocation_" + request.getSoId(), ".pdf");
		file.deleteOnExit();
		log.info("path is file == " + file.getAbsolutePath());

		if (partyWise.isEmpty()) {
			return file;
		}

		String part = resolvePart(request, partyWise);
		List<Integer> allocationIds = new ArrayList<>();

		try (OutputStream os = new FileOutputStream(file)) {
			writePdf(os, part, partyWise.values(), allocationIds);
			if (isNewPart(request) && !allocationIds.isEmpty()) {
				log.info("nextPartValue == " + part);
				soAllocationRepo.updatePDFGenerationPart(part, allocationIds);
			}
		} catch (Exception ex) {
			log.error("PDF generation error for soId=" + request.getSoId(), ex);
		}
		return file;
	}

	// =====================================================================
	// 1b. Download PDF – separate PDF per party, all returned as base64 in one response
	//     Same flow / queries as emailPdfToParties: soDetailsBySoId + findPartyEmails
	// =====================================================================

	@Override
	public ResponseEntity<Object> downloadPartyWisePdfs(ListPageSearchRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (request == null || request.getSoId() == null || request.getSoId() <= 0) {
			return new ResponseEntity<>(result("fail", "soId is required"), headers, HttpStatus.BAD_REQUEST);
		}
		Integer soId = request.getSoId();

		try {
			// 1. Load allocations grouped by processing-center party
			Map<Integer, SalesOrderListResponse> partyWise = soDetailsBySoId(request);
			if (partyWise.isEmpty()) {
				log.error("downloadPartyWisePdfs: no allocations found for soId {} part {}", soId, request.getPdfGenerationPart());
				return new ResponseEntity<>(result("fail", "No allocations found for soId " + soId
						+ (isNewPart(request) ? "" : " and part " + request.getPdfGenerationPart())), headers, HttpStatus.NOT_FOUND);
			}
			String part = resolvePart(request, partyWise);
			String soNumber = partyWise.values().iterator().next().getSoNumber();

			// 2. Resolve party details for all parties in one query
			Map<Integer, PartyContact> contacts = loadPartyContacts(partyWise.keySet());

			List<Map<String, Object>> partyResults = new ArrayList<>();
			List<Integer> generatedAllocationIds = new ArrayList<>();
			int generatedCount = 0;

			for (Map.Entry<Integer, SalesOrderListResponse> entry : partyWise.entrySet()) {
				Integer partyId = entry.getKey();
				SalesOrderListResponse block = entry.getValue();
				PartyContact contact = contacts.get(partyId);
				String partyName = (contact != null && StringUtils.hasText(contact.partyName)) ? contact.partyName : block.getPartyName();

				Map<String, Object> partyResult = new LinkedHashMap<>();
				partyResult.put("partyId", NO_PARTY_KEY.equals(partyId) ? null : partyId);
				partyResult.put("partyName", partyName);
				partyResult.put("email1", contact != null ? trimToNull(contact.email1) : null);
				partyResult.put("email2", contact != null ? trimToNull(contact.email2) : null);
				partyResults.add(partyResult);

				if (NO_PARTY_KEY.equals(partyId)) {
					log.warn("downloadPartyWisePdfs: warehouse not mapped to a party for soId {}", soId);
				}

				// 3. Generate this party's PDF (in memory)
				List<Integer> partyAllocationIds = new ArrayList<>();
				byte[] pdfBytes;
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					writePdf(bos, part, Collections.singletonList(block), partyAllocationIds);
					pdfBytes = bos.toByteArray();
				} catch (Exception e) {
					log.error("downloadPartyWisePdfs: PDF generation failed for soId {} partyId {}", soId, partyId, e);
					partyResult.put("status", "fail");
					partyResult.put("message", "PDF generation failed: " + e.getMessage());
					continue;
				}

				// 4. Encode as base64
				String fileName = buildFileName(soNumber, part, partyName);
				partyResult.put("fileName", fileName);
				partyResult.put("contentType", "application/pdf");
				partyResult.put("sizeInBytes", pdfBytes.length);
				partyResult.put("encodedFile", Base64.getEncoder().encodeToString(pdfBytes));
				partyResult.put("status", "success");
				partyResult.put("message", "PDF generated successfully");
				log.info("soId== {}, partyId== {}, fileName== {}, bytes== {}", soId, partyId, fileName, pdfBytes.length);

				generatedAllocationIds.addAll(partyAllocationIds);
				generatedCount++;
			}

			int total = partyWise.size();
			String status = generatedCount == total ? "success" : (generatedCount > 0 ? "partial" : "fail");
			Map<String, Object> body = result(status, generatedCount + " of " + total + " party PDF(s) generated");
			body.put("soId", soId);
			body.put("soNumber", soNumber);
			body.put("pdfGenerationPart", part);
			body.put("results", partyResults);

			return new ResponseEntity<>(body, headers, generatedCount > 0 ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {
			log.error("downloadPartyWisePdfs failed for soId {}", soId, e);
			return new ResponseEntity<>(result("fail", e.getMessage()), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =====================================================================
	// 2. Email PDF – separate PDF + separate mail per party
	//    party email1 -> To, email2 -> CC (email2 promoted to To if email1 is blank)
	// =====================================================================

	@Override
	public ResponseEntity<Object> emailPdfToParties(ListPageSearchRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (request == null || request.getSoId() == null || request.getSoId() <= 0) {
			return new ResponseEntity<>(result("fail", "soId is required"), headers, HttpStatus.BAD_REQUEST);
		}
		Integer soId = request.getSoId();

		try {
			// 1. Load allocations grouped by processing-center party
			Map<Integer, SalesOrderListResponse> partyWise = soDetailsBySoId(request);
			if (partyWise.isEmpty()) {
				log.error("emailPdfToParties: no allocations found for soId {} part {}", soId, request.getPdfGenerationPart());
				return new ResponseEntity<>(result("fail", "No allocations found for soId " + soId
						+ (isNewPart(request) ? "" : " and part " + request.getPdfGenerationPart())), headers, HttpStatus.NOT_FOUND);
			}
			String part = resolvePart(request, partyWise);
			String soNumber = partyWise.values().iterator().next().getSoNumber();

			// 2. Resolve recipients for all parties in one query
			Map<Integer, PartyContact> contacts = loadPartyContacts(partyWise.keySet());

			List<Map<String, Object>> partyResults = new ArrayList<>();
			List<Integer> sentAllocationIds = new ArrayList<>();
			int sentCount = 0;

			for (Map.Entry<Integer, SalesOrderListResponse> entry : partyWise.entrySet()) {
				Integer partyId = entry.getKey();
				SalesOrderListResponse block = entry.getValue();
				PartyContact contact = contacts.get(partyId);
				String partyName = (contact != null && StringUtils.hasText(contact.partyName)) ? contact.partyName : block.getPartyName();

				Map<String, Object> partyResult = new LinkedHashMap<>();
				partyResult.put("partyId", NO_PARTY_KEY.equals(partyId) ? null : partyId);
				partyResult.put("partyName", partyName);
				partyResults.add(partyResult);

				if (NO_PARTY_KEY.equals(partyId)) {
					log.warn("emailPdfToParties: warehouse not mapped to a party for soId {}", soId);
					partyResult.put("status", "skipped");
					partyResult.put("message", "Warehouse is not mapped to any party");
					continue;
				}

				String toEmails = contact != null ? trimToNull(contact.email1) : null;
				String ccEmails = contact != null ? trimToNull(contact.email2) : null;
				if (toEmails == null && ccEmails != null) {
					// No primary party email: promote CC
					toEmails = ccEmails;
					ccEmails = null;
				}
				partyResult.put("to", toEmails);
				partyResult.put("cc", ccEmails);

				if (toEmails == null) {
					log.warn("emailPdfToParties: no email1/email2 for partyId {} ({})", partyId, partyName);
					partyResult.put("status", "skipped");
					partyResult.put("message", "No email1/email2 configured for party");
					continue;
				}
				log.info("soId== {}, partyId== {}, toEmails== {}, ccEmails== {}", soId, partyId, toEmails, ccEmails);

				// 3. Generate this party's PDF (in memory)
				List<Integer> partyAllocationIds = new ArrayList<>();
				byte[] pdfBytes;
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					writePdf(bos, part, Collections.singletonList(block), partyAllocationIds);
					pdfBytes = bos.toByteArray();
				} catch (Exception e) {
					log.error("emailPdfToParties: PDF generation failed for soId {} partyId {}", soId, partyId, e);
					partyResult.put("status", "fail");
					partyResult.put("message", "PDF generation failed: " + e.getMessage());
					continue;
				}

				// 4. Send email with PDF attachment
				String fileName = buildFileName(soNumber, part, partyName);
				boolean sent = sendSOPdfMail(toEmails, ccEmails, soNumber, part, partyName, fileName, pdfBytes);
				if (!sent) {
					partyResult.put("status", "fail");
					partyResult.put("message", "Failed to send email");
					continue;
				}

				sentAllocationIds.addAll(partyAllocationIds);
				sentCount++;
				partyResult.put("status", "success");
				partyResult.put("message", "Emailed successfully to " + toEmails + (ccEmails != null ? ", cc " + ccEmails : ""));
			}

			int total = partyWise.size();
			String status = sentCount == total ? "success" : (sentCount > 0 ? "partial" : "fail");
			Map<String, Object> body = result(status, sentCount + " of " + total + " parties emailed");
			body.put("soId", soId);
			body.put("soNumber", soNumber);
			body.put("pdfGenerationPart", part);
			body.put("results", partyResults);

			return new ResponseEntity<>(body, headers, sentCount > 0 ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {
			log.error("emailPdfToParties failed for soId {}", soId, e);
			return new ResponseEntity<>(result("fail", e.getMessage()), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean sendSOPdfMail(String toEmails, String ccEmails, String soNumber, String part, String partyName,
			String fileName, byte[] pdfBytes) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
			if (StringUtils.hasText(mailFrom)) {
				helper.setFrom(mailFrom);
			}

			String subject = buildSubject(soNumber, part, partyName);
			if (StringUtils.hasText(mailOverrideTo)) {
				// Non-prod: redirect everything to the override address
				log.warn("sendSOPdfMail redirected | intended to== {}, cc== {} | actual== {}", toEmails, ccEmails, mailOverrideTo);
				helper.setTo(parseAddresses(mailOverrideTo));
				subject = "[TEST -> " + toEmails + (ccEmails != null ? " cc " + ccEmails : "") + "] " + subject;
			} else {
				helper.setTo(parseAddresses(toEmails));
				String cc = joinNonBlank(ccEmails, mailCc);
				if (cc != null) {
					helper.setCc(parseAddresses(cc));
				}
			}

			helper.setSubject(subject);
			helper.setText(buildBody(soNumber, part, partyName), true);
			helper.addAttachment(fileName, new ByteArrayResource(pdfBytes), "application/pdf");
			mailSender.send(message);
			return true;
		} catch (Exception e) {
			log.error("sendSOPdfMail failed for soNumber {} party {} to {}", soNumber, partyName, toEmails, e);
			return false;
		}
	}

	private Map<String, Object> result(String status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status);
		body.put("message", message);
		return body;
	}

	/** Accepts comma or semicolon separated addresses. */
	private InternetAddress[] parseAddresses(String emails) throws AddressException {
		return InternetAddress.parse(emails.replace(';', ','), true);
	}

	private String joinNonBlank(String a, String b) {
		String x = trimToNull(a);
		String y = trimToNull(b);
		if (x == null) {
			return y;
		}
		return y == null ? x : x + "," + y;
	}

	private static String trimToNull(String s) {
		return (s == null || s.trim().isEmpty()) ? null : s.trim();
	}

	private String buildSubject(String soNumber, String part, String partyName) {
		return "JSW One - Allocated Processing Orders - " + nz(soNumber) + " - Part " + part + " - " + nz(partyName);
	}

	private String buildBody(String soNumber, String part, String partyName) {
		return "<p>Dear " + HtmlUtils.htmlEscape(nz(partyName)) + ",</p>"
				+ "<p>Please find attached the allocated processing orders for Order No <b>"
				+ HtmlUtils.htmlEscape(nz(soNumber)) + "</b> (Part <b>" + HtmlUtils.htmlEscape(part) + "</b>).</p>"
				+ "<p>Regards,<br/>JSW One Distribution Limited</p>"
				+ "<p style='color:#888;font-size:11px'>This is a system generated email. Please do not reply.</p>";
	}

	private String buildFileName(String soNumber, String part, String partyName) {
		String raw = "SO_" + nz(soNumber) + "_" + part + "_" + nz(partyName);
		return raw.replaceAll("[^A-Za-z0-9_-]+", "_") + ".pdf";
	}

	private Map<Integer, PartyContact> loadPartyContacts(Collection<Integer> partyIds) {
		Set<Integer> ids = partyIds.stream().filter(Objects::nonNull).filter(id -> id > 0).collect(Collectors.toSet());
		Map<Integer, PartyContact> map = new HashMap<>();
		if (ids.isEmpty()) {
			return map;
		}
		for (Object[] row : salesOrderRepository.findPartyEmails(ids)) {
			PartyContact c = new PartyContact();
			c.partyName = row[1] != null ? row[1].toString() : null;
			c.email1 = row[2] != null ? row[2].toString() : null;
			c.email2 = row[3] != null ? row[3].toString() : null;
			map.put(((Number) row[0]).intValue(), c);
		}
		return map;
	}

	private static class PartyContact {
		String partyName;
		String email1;
		String email2;
	}

	// =====================================================================
	// Shared PDF rendering
	// =====================================================================

	/** Header once, then one table + page per party block. Collects allocation ids rendered. */
	private void writePdf(OutputStream os, String part, Collection<SalesOrderListResponse> blocks,
			List<Integer> allocationIds) throws Exception {

		Document document = new Document(PageSize.A4.rotate());
		try {
			PdfWriter.getInstance(document, os);
			document.open();

			Font font9 = FontFactory.getFont(BaseFont.WINANSI, 9f);
			Font font9b = FontFactory.getFont(BaseFont.WINANSI, 9f, Font.BOLD);
			Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);

			addHeader(document, part, font12b);

			for (SalesOrderListResponse block : blocks) {
				addPartyTable(document, block, font9, font9b, allocationIds);
				document.newPage();
			}
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	private void addHeader(Document document, String part, Font font12b) throws Exception {
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);

		Image logo = Image.getInstance(imagelogopath + File.separator + "jswone_logo.jpg");
		logo.scaleToFit(70, 70);

		PdfPCell logoCell = new PdfPCell(logo);
		logoCell.setBorder(Rectangle.NO_BORDER);

		PdfPCell titleCell = new PdfPCell(new Paragraph("JSW One Distribution Limited", font12b));
		titleCell.setBorder(Rectangle.NO_BORDER);

		headerTable.addCell(logoCell);
		headerTable.addCell(titleCell);

		document.add(headerTable);
		document.add(Chunk.NEWLINE);

		Paragraph title = new Paragraph(part + " - Allocated Processing Orders", font12b);
		title.setLeading(20f);
		document.add(title);
		document.add(Chunk.NEWLINE);
	}

	private void addPartyTable(Document document, SalesOrderListResponse entity, Font font9, Font font9b,
			List<Integer> allocationIds) throws Exception {

		PdfPTable table = new PdfPTable(12);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 3, 2, 4, 2, 2, 1, 2, 2, 2, 2, 2, 2 });

		// ===== META ROW =====
		table.addCell(createMetaCell(entity.getPartyName(), "Processing Center", font9, font9b, 2));
		table.addCell(createMetaCell("Order No", entity.getSoNumber(), font9, font9b, 2));
		table.addCell(createMetaCell("CP No", "", font9, font9b, 2));
		table.addCell(createMetaCell("Order Date", entity.getOrderDate(), font9, font9b, 2));
		table.addCell(createMetaCell("CP Date", "", font9, font9b, 2));

		PdfPCell emptyMeta = new PdfPCell(new Phrase(""));
		emptyMeta.setColspan(6);
		emptyMeta.setBorder(Rectangle.NO_BORDER);
		table.addCell(emptyMeta);

		// ===== HEADER ROWS =====
		table.addCell(getHeaderCell("Coil No", 2));
		table.addCell(getHeaderCell("Coil Batch No", 2));
		table.addCell(getHeaderCell("Product Details", 2));
		table.addCell(getHeaderCell("Packing Mode", 2));
		table.addCell(getHeaderCell("Planned Qty", 2));
		table.addCell(getHeaderCell("Planned Qty Nos", 2));
		table.addCell(getHeaderCell("Processing Center", 2));

		PdfPCell tolCell = new PdfPCell(new Phrase("Tolerances", font9b));
		tolCell.setColspan(3);
		tolCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(tolCell);

		table.addCell(getHeaderCell("Action Status", 2));
		table.addCell(getHeaderCell("Special Instructions", 2));

		table.addCell(getSubHeaderCell("Diagonal(mm)", font9b));
		table.addCell(getSubHeaderCell("Width(mm)", font9b));
		table.addCell(getSubHeaderCell("Edge Burr(mm)", font9b));

		Integer previousChildId = null;

		// ===== CHILD LOOP =====
		for (SalesOrderListDTO obj : entity.getChildListResp()) {
			if (obj.getSoAllocationId() > 0) {
				allocationIds.add(obj.getSoAllocationId());
			}
			if (!Objects.equals(obj.getSoChildId(), previousChildId)) {
				PdfPCell mergedCell = new PdfPCell(new Phrase(obj.getMaterialDesc(), font9));
				mergedCell.setColspan(4);
				mergedCell.setFixedHeight(30f);
				table.addCell(mergedCell);
				table.addCell(new PdfPCell(new Phrase(String.valueOf(obj.getFweight()), font9)));
				PdfPCell blank = new PdfPCell(new Phrase(""));
				blank.setColspan(7);
				table.addCell(blank);
			}

			table.addCell(new PdfPCell(new Phrase(obj.getCoilNo(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getCustomerBatchNo(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getMaterialDetails(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getPackingMode(), font9)));
			table.addCell(new PdfPCell(new Phrase("" + obj.getAllocatedQty(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getPlannedNoofPieces() > 0 ? String.valueOf(obj.getPlannedNoofPieces()) : "", font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getPartyName(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getDiagonal(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getWidth(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getEdgeBurr(), font9)));
			table.addCell(new PdfPCell(new Phrase(obj.getPacketStatus(), font9)));
			table.addCell(new PdfPCell(new Phrase("", font9)));
			previousChildId = obj.getSoChildId();
		}

		PdfPCell notesCell = new PdfPCell(new Phrase("Notes:", font9));
		notesCell.setColspan(12);
		table.addCell(notesCell);

		document.add(table);
	}

	// =====================================================================
	// Part resolution
	// =====================================================================

	private boolean isNewPart(ListPageSearchRequest request) {
		return !StringUtils.hasText(request.getPdfGenerationPart());
	}

	/** Uses requested part for re-send; otherwise next letter after already-generated parts (A, B, C ...). */
	private String resolvePart(ListPageSearchRequest request, Map<Integer, SalesOrderListResponse> partyWise) {
		if (!isNewPart(request)) {
			return request.getPdfGenerationPart().trim();
		}
		int partsLength = 0;
		SalesOrderListResponse first = partyWise.values().iterator().next();
		if (StringUtils.hasText(first.getPdfGenerationPart())) {
			partsLength = (int) Arrays.stream(first.getPdfGenerationPart().split(","))
					.map(String::trim).filter(p -> !p.isEmpty()).count();
		}
		return String.valueOf((char) ('A' + partsLength));
	}

	// =====================================================================
	// Data
	// =====================================================================

	/** Groups SO allocation rows by processing-center party id (wm.party_id, column 30). */
	private Map<Integer, SalesOrderListResponse> soDetailsBySoId(ListPageSearchRequest request) {
		log.info("getPdfGenerationPart == " + request.getPdfGenerationPart());
		List<Object[]> packetsList = salesOrderRepository.soDetailsBySoIdEmail(request.getSoId());
		Map<Integer, SalesOrderListResponse> responseMap = new LinkedHashMap<>();

		for (Object[] result : packetsList) {

			String partyName = result[21] != null ? (String) result[21] : "UNKNOWN";
			Integer partyId = result.length > 30 && result[30] != null ? ((Number) result[30]).intValue() : NO_PARTY_KEY;

			SalesOrderListResponse resp = responseMap.get(partyId);
			if (resp == null) {
				resp = new SalesOrderListResponse();
				resp.setPartyName(partyName);
				resp.setSoStatus(result[12] != null ? (String) result[12] : null);
				resp.setSoNumber(result[1] != null ? (String) result[1] : null);
				resp.setOrderDate(result[7] != null ? (String) result[7] : null);
				resp.setChildListResp(new ArrayList<>());
				resp.setPdfGenerationPart(result[29] != null ? (String) result[29] : "");
				responseMap.put(partyId, resp);
			}

			SalesOrderListDTO child = new SalesOrderListDTO();
			child.setSoChildId(result[6] != null ? (Integer) result[6] : null);
			child.setSoAllocationId(result[15] != null ? Integer.parseInt(result[15].toString()) : null);
			child.setMaterialDesc(result[2] != null ? (String) result[2] : null);
			child.setCoilNo(result[17] != null ? (String) result[17] : null);
			child.setCustomerBatchNo(result[18] != null ? (String) result[18] : null);
			child.setPlannedNoofPieces(result[19] != null ? ((Number) result[19]).intValue() : 0);
			child.setPackingMode(result[20] != null ? (String) result[20] : "");
			child.setFweight(result[10] != null ? ((BigDecimal) result[10]).floatValue() : null);
			child.setAllocatedQty(result[16] != null ? ((BigDecimal) result[16]).floatValue() : null);
			child.setPartyName(partyName);

			child.setDiagonal("Max. 3.00");
			child.setEdgeBurr("Max 3%");
			child.setWidth("+5/-0.0");

			child.setPacketStatus(result[22] != null ? (String) result[22] : "");

			String materialGrade = result[26] != null ? (String) result[26] : "";
			String materialDesc = result[27] != null ? (String) result[27] : "";
			child.setFwidth(result[23] != null ? (Float) result[23] : null);
			child.setFthickness(result[25] != null ? (Float) result[25] : null);
			child.setMaterialDetails(materialGrade + materialDesc + " * " + child.getFthickness() + " * " + child.getFwidth());

			resp.getChildListResp().add(child);
		}
		return responseMap;
	}

	// =====================================================================
	// Cell helpers
	// =====================================================================

	private PdfPCell createMetaCell(String title, String value, Font font, Font bold, int colspan) {
		PdfPCell cell = new PdfPCell();
		Paragraph p = new Paragraph();
		p.add(new Chunk(title != null ? title : "", bold));
		p.add(Chunk.NEWLINE);
		p.add(new Chunk(value != null ? value : "", font));
		cell.addElement(p);
		cell.setColspan(colspan);
		return cell;
	}

	private PdfPCell getHeaderCell(String text, int rowspan) {
		Font font = FontFactory.getFont(BaseFont.WINANSI, 9f, Font.BOLD);
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setRowspan(rowspan);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		return cell;
	}

	private PdfPCell getSubHeaderCell(String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(5);
		return cell;
	}

	public static String findFieldValue(List<QIRTemplateDtlsJsonArrayDTO> templateDetailsList, String fieldName) {
		String resp = "";
		for (QIRTemplateDtlsJsonArrayDTO dto : templateDetailsList) {
			if (fieldName.equals(dto.getType())) {
				resp = "" + dto.getValue();
			}
		}
		return resp;
	}

	private static String nz(String s) {
		return s != null ? s : "";
	}
}
