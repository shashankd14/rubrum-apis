package com.steel.product.jswone.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import com.lowagie.text.DocumentException;
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


	@Value("${imagelogopath}")
	private String imagelogopath;

	@Autowired
	private SalesOrderJswRepository salesOrderRepository;

	@Autowired
	private SalesOrderAllocationJswRepository soAllocationRepo;
	
	@Override
	public File generatePdf(ListPageSearchRequest request) {

		File file = null;
		try {
			file = generateSOPdf(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private File generateSOPdf(ListPageSearchRequest request) throws IOException, DocumentException {

		Map<String, SalesOrderListResponse> mapp = soDetailsBySoId(request);

		File file = File.createTempFile("qirpdf_" + request.getSoId(), ".pdf");
		Document document = new Document(PageSize.A4.rotate());

		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));

			String nextPartValue=request.getPdfGenerationPart();
			
			if (request.getPdfGenerationPart() != null && request.getPdfGenerationPart().length() > 0) {
				nextPartValue = request.getPdfGenerationPart();
			} else {
				for (Map.Entry<String, SalesOrderListResponse> entry : mapp.entrySet()) {
					SalesOrderListResponse entity = entry.getValue();
					String pdfGenerationPart = entity.getPdfGenerationPart();
					String[] parts = pdfGenerationPart.split(",");

					if (parts.length == 1) {
						nextPartValue = "A";
					}
					if (parts.length == 2) {
						nextPartValue = "B";
					}
					if (parts.length == 3) {
						nextPartValue = "C";
					}
					if (parts.length == 4) {
						nextPartValue = "D";
					}
					if (parts.length == 5) {
						nextPartValue = "E";
					}
					if (parts.length == 6) {
						nextPartValue = "F";
					}
					if (parts.length == 7) {
						nextPartValue = "G";
					}
					break;
				}
			}
			
			if (mapp != null && mapp.size() > 0) {

				document.open();
				Font font9 = FontFactory.getFont(BaseFont.WINANSI, 9f);
				Font font9b = FontFactory.getFont(BaseFont.WINANSI, 9f, Font.BOLD);
				Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);

				// ===== COMMON HEADER (LOGO + TITLE) =====
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

				Paragraph title = new Paragraph(nextPartValue+" - Allocated Processing Orders", font12b);
				title.setLeading(20f);
				document.add(title);
				document.add(Chunk.NEWLINE);
				List<Integer> allocationIdsList = new ArrayList<>();

				for (Map.Entry<String, SalesOrderListResponse> entry : mapp.entrySet()) {

					SalesOrderListResponse entity = entry.getValue();

					// ================= NEW TABLE PER PARTY =================
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

					int oldallocationid = 0;

					// ===== CHILD LOOP =====
					for (SalesOrderListDTO obj : entity.getChildListResp()) {
						allocationIdsList.add(obj.getSoAllocationId());
						if (obj.getSoChildId() != oldallocationid) {
							PdfPCell mergedCell = new PdfPCell(new Phrase(obj.getMaterialDesc(), font9));
							mergedCell.setColspan(4);
							mergedCell.setFixedHeight(30f); // sets fixed height
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
						table.addCell(new PdfPCell(new Phrase(obj.getPlannedNoofPieces() > 0 ? String.valueOf(obj.getPlannedNoofPieces()) : "",	font9)));
						table.addCell(new PdfPCell(new Phrase(obj.getPartyName(), font9)));
						table.addCell(new PdfPCell(new Phrase(obj.getDiagonal(), font9)));
						table.addCell(new PdfPCell(new Phrase(obj.getWidth(), font9)));
						table.addCell(new PdfPCell(new Phrase(obj.getEdgeBurr(), font9)));
						table.addCell(new PdfPCell(new Phrase(obj.getPacketStatus(), font9)));
						table.addCell(new PdfPCell(new Phrase("", font9)));
						oldallocationid = obj.getSoChildId();
					}

					PdfPCell notesCell = new PdfPCell(new Phrase("Notes:", font9));
					notesCell.setColspan(12);
					table.addCell(notesCell);

					document.add(table);
					document.newPage();
				}
				if (!(request.getPdfGenerationPart() != null && request.getPdfGenerationPart().length() > 0)) {
					log.info("Hi nextPartValue == " + nextPartValue);
					soAllocationRepo.updatePDFGenerationPart(nextPartValue, allocationIdsList);
				}
				document.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		file.deleteOnExit();
		return file;
	}
	
	private PdfPCell createMetaCell(String title, String value, Font font, Font bold, int colspan) {
	    PdfPCell cell = new PdfPCell();
	    Paragraph p = new Paragraph();
	    p.add(new Chunk(title, bold));
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

	private PdfPCell getMetaCell(String label, String value, Font valueFont, Font labelFont) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(label + "\n", labelFont));
		phrase.add(new Chunk(value != null ? value : "", valueFont));

		PdfPCell cell = new PdfPCell(phrase);
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

	private Map<String, SalesOrderListResponse> soDetailsBySoId(ListPageSearchRequest request) {
		System.out.println("getPdfGenerationPart == "+request.getPdfGenerationPart());
		List<Object[]> packetsList = salesOrderRepository.soDetailsBySoId(request.getSoId(), request.getPdfGenerationPart());
	    Map<String, SalesOrderListResponse> responseMap = new HashMap<>();

	    for (Object[] result : packetsList) {

	        String partyName = result[21] != null ? (String) result[21] : "UNKNOWN";

	        // Get or create response for this party
	        SalesOrderListResponse resp = responseMap.get(partyName);

	        if (resp == null) {
	            resp = new SalesOrderListResponse();

	            resp.setPartyName(partyName);
	            resp.setSoStatus(result[12] != null ? (String) result[12] : null);
	            resp.setSoNumber(result[1] != null ? (String) result[1] : null);
	            resp.setOrderDate(result[7] != null ? (String) result[7] : null);
	            resp.setChildListResp(new ArrayList<>()); // IMPORTANT
	            resp.setPdfGenerationPart((result[29] != null ? (String) result[29] : ""));
	            responseMap.put(partyName, resp);
	        }

	        // Create child object
	        SalesOrderListDTO child = new SalesOrderListDTO();

			child.setSoChildId(result[6] != null ? (Integer) result[6] : null);
			child.setSoAllocationId(result[15] != null ? Integer.parseInt(result[15].toString()) : null);
			child.setMaterialDesc(result[2] != null ? (String) result[2] : null);
	        child.setCoilNo(result[17] != null ? (String) result[17] : null);
	        child.setCustomerBatchNo(result[18] != null ? (String) result[18] : null);
	        child.setPlannedNoofPieces(result[19] != null ? ((Number) result[19]).intValue() : 0);
	        child.setPackingMode(result[20] != null ? (String) result[20] : "");
	        child.setFweight(result[10] != null ? ((BigDecimal) result[10]).floatValue() : null);
	        child.setAllocatedQty(result[11] != null ? ((BigDecimal) result[11]).floatValue() : null);
	        child.setPartyName(partyName);

	        child.setDiagonal("Max. 3.00");
	        child.setEdgeBurr("Max 3%");
	        child.setWidth("+5/-0.0");

	        child.setPacketStatus(result[22] != null ? (String) result[22] : "");

	        String materialGrade = (result[26] != null ? (String) result[26] : "");
	        String materialDesc = (result[27] != null ? (String) result[27] : "");
	        child.setFwidth(result[23] != null ? (Float) result[23] : null);
	        child.setFthickness(result[25] != null ? (Float) result[25] : null);

	        String coilSKU = materialGrade + materialDesc + " * " + child.getFthickness() + " * " + child.getFwidth();

	        child.setMaterialDetails(coilSKU);

	        // Add child to respective party
	        resp.getChildListResp().add(child);
	    }

	    return responseMap;
	}
}
