package com.steel.product.application.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.steel.product.application.dao.InstructionRepository;
import com.steel.product.application.dao.PartDetailsRepository;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.pdf.LabelPrintDTO;
import com.steel.product.application.dto.qrcode.QRCodeResponse;
import com.steel.product.application.mapper.InstructionMapper;
import com.steel.product.application.mapper.PartDetailsMapper;

@Service
public class LabelPrintPDFGenerator {

	private final static Logger logger = LoggerFactory.getLogger("LabelPrintPDFGenerator");

    private static final DecimalFormat decfor = new DecimalFormat("0.00");  

    @Autowired InstructionRepository instructionRepository;

    @Autowired PartDetailsRepository partDetailsRepository;

	@Autowired PartDetailsMapper partDetailsMapper;

	@Autowired InstructionMapper instructionMapper;

	public File renderInwardLabelPrintPDF(LabelPrintDTO labelPrintDTO, QRCodeResponse resp, File labelFile) throws IOException, DocumentException {
		logger.info("renderLabelPrintPDF ");
		Document document = new Document();

		int tableRowHeight = 20;
		try {
			
			Rectangle myPagesize = new Rectangle (284, 213);
			FileOutputStream fos = new FileOutputStream(labelFile);
			document = new Document(myPagesize, 2f, 2f, 3f, 2f);

			PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
			pdfWriter.setBoxSize("art", myPagesize);
			document.open();

			Font font5 = FontFactory.getFont(BaseFont.WINANSI, 5f);
			Font font7b = FontFactory.getFont(BaseFont.WINANSI, 7f, Font.BOLD);
			Font font8b = FontFactory.getFont(BaseFont.WINANSI, 8f, Font.BOLD);
			Font font11b = FontFactory.getFont(BaseFont.WINANSI, 10f, Font.BOLD);
			Font font11u = FontFactory.getFont(BaseFont.WINANSI, 11f, Font.UNDERLINE | Font.BOLD);
			Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);
			
			PdfPTable coilDetailsTab = new PdfPTable(4);
			coilDetailsTab.setWidthPercentage(100);
			coilDetailsTab.setWidths(new int[] {40, 100, 70, 70});

			PdfPCell companyNameCell = new PdfPCell(new Phrase("ASPEN STEEL PVT LTD", font11u));
			companyNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
			//companyNameCell.setFixedHeight(17);
			companyNameCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
			companyNameCell.setColspan(4);
			coilDetailsTab.addCell(companyNameCell);	

			PdfPCell addressCell2 = new PdfPCell(new Phrase("Email : aspen.bidadi@gmail.com", font5));
			addressCell2.setHorizontalAlignment( Element.ALIGN_CENTER);
			addressCell2.setVerticalAlignment( Element.ALIGN_TOP);
			addressCell2.setFixedHeight(9);
			addressCell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			addressCell2.setColspan(4);
			coilDetailsTab.addCell(addressCell2);

			PdfPTable unitDetailsTab = new PdfPTable(3);
			unitDetailsTab.setWidthPercentage(100);
			unitDetailsTab.setWidths(new int[] {90, 90, 90});

			PdfPCell unit1Cell = new PdfPCell(new Phrase(new Chunk("UNIT - I", font7b)));
			unit1Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
			unit1Cell.setVerticalAlignment( Element.ALIGN_TOP);
			unit1Cell.setBorder( Rectangle.NO_BORDER);
			unitDetailsTab.addCell(unit1Cell);	

			PdfPCell unit2Cell = new PdfPCell(new Phrase(new Chunk("UNIT - II", font7b)));
			unit2Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
			unit2Cell.setVerticalAlignment( Element.ALIGN_TOP);
			unit2Cell.setBorder( Rectangle.NO_BORDER);
			unitDetailsTab.addCell(unit2Cell);	

			PdfPCell unit3Cell = new PdfPCell(new Phrase(new Chunk("UNIT - III", font7b)));
			unit3Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
			unit3Cell.setVerticalAlignment( Element.ALIGN_TOP);
			unit3Cell.setBorder( Rectangle.NO_BORDER);
			unitDetailsTab.addCell(unit3Cell);	

			PdfPCell addressCELL = new PdfPCell();
			addressCELL.setHorizontalAlignment(Element.ALIGN_CENTER);
			addressCELL.setColspan(4);
			addressCELL.setVerticalAlignment(Element.ALIGN_MIDDLE);
			addressCELL.setFixedHeight(15);
			addressCELL.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			addressCELL.addElement(unitDetailsTab);
			coilDetailsTab.addCell(addressCELL);

			/*BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
            int barcodeHeight = 17;
            Barcode barcode = new Barcode128();
            barcode.setCode(labelPrintDTO.getId());
			barcode.setCodeType(Barcode.CODE128);
			barcode.setBarHeight(barcodeHeight);
			barcode.setX(1.0f); //Adjust the width of the barcode bars
 			Image barcodeImage = barcode.createImageWithBarcode(pdfWriter.getDirectContent(), null, null);*/
 			
			PdfPCell companyNameCell1 = new PdfPCell(new Phrase(new Chunk("RM", font12b)));			
			companyNameCell1.setHorizontalAlignment( Element.ALIGN_CENTER);
			companyNameCell1.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell1.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell1);	
			
			Paragraph custNameParagraph = new Paragraph();
			custNameParagraph.add(new Phrase(new Chunk("CUST NAME: ", font8b)));
			custNameParagraph.add(new Phrase(new Chunk(resp.getPartyName().toUpperCase(), font11b)));
			PdfPCell companyNameCell2 = new PdfPCell(custNameParagraph);
			companyNameCell2.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell2.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell2.setFixedHeight(tableRowHeight);
			companyNameCell2.setColspan(2);
			coilDetailsTab.addCell(companyNameCell2);	

			Paragraph dateParagraph = new Paragraph();
			dateParagraph.add(new Phrase(new Chunk("INWARD DT:  ", font8b)));
			dateParagraph.add(new Phrase(new Chunk(resp.getReceivedDate(), font11b)));	
			PdfPCell companyNameCell8 = new PdfPCell(dateParagraph);
			companyNameCell8.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell8.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell8.setFixedHeight(24);
			coilDetailsTab.addCell(companyNameCell8);	

			Paragraph coilParagraph = new Paragraph();
			coilParagraph.add(new Phrase(new Chunk("ASPL COIL NO: ", font8b)));
			coilParagraph.add(new Phrase(new Chunk(resp.getCoilNo(), font11b)));
			PdfPCell companyNameCell3 = new PdfPCell(coilParagraph);
			companyNameCell3.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell3.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell3.setColspan(2);
			companyNameCell3.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell3);	

			Paragraph specParagraph = new Paragraph();
			specParagraph.add(new Phrase(new Chunk("SPEC: ", font8b)));
			specParagraph.add(new Phrase(new Chunk(resp.getMaterialDesc(), font11b)));
			PdfPCell companyNameCell4 = new PdfPCell(specParagraph);
			companyNameCell4.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell4.setColspan(2);
			companyNameCell4.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell4.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell4);		

			Paragraph mcoilParagraph = new Paragraph();
			mcoilParagraph.add(new Phrase(new Chunk("CUST COIL NO: ", font8b)));
			mcoilParagraph.add(new Phrase(new Chunk(resp.getCustomerBatchNo(), font11b)));			
			PdfPCell companyNameCell5 = new PdfPCell(mcoilParagraph);
			companyNameCell5.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell5.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell5.setColspan(2);
			companyNameCell5.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell5);	

			Paragraph gradeParagraph = new Paragraph();
			gradeParagraph.add(new Phrase(new Chunk("GRADE: ", font8b)));
			gradeParagraph.add(new Phrase(new Chunk(resp.getMaterialGrade(), font11b)));	
			PdfPCell companyNameCell6 = new PdfPCell(gradeParagraph);
			companyNameCell6.setColspan(2);
			companyNameCell6.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell6.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell6.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell6);		
			
			PdfPCell companyNameCell7 = new PdfPCell(new Phrase("T: "+resp.getFthickness()+"        "+"W: "+resp.getFwidth()+"       "+"L: "+resp.getFlength(), font11b));
			companyNameCell7.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell7.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell7.setColspan(4);
			companyNameCell7.setFixedHeight(tableRowHeight);
			coilDetailsTab.addCell(companyNameCell7);		
			
			Paragraph grosswtParagraph = new Paragraph();
			grosswtParagraph.add(new Phrase(new Chunk("Gross Wt(kgs):  ", font8b)));
			grosswtParagraph.add(new Phrase(new Chunk(resp.getGrossWeight(), font11b)));
			PdfPCell companyNameCell9 = new PdfPCell(grosswtParagraph);
			companyNameCell9.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell9.setColspan(2);
			companyNameCell9.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell9.setFixedHeight(23);
			coilDetailsTab.addCell(companyNameCell9);	
			
			Image qrCodeImage = Image.getInstance(getQRCodeInward(resp));
			PdfPCell companyNameCell13 = new PdfPCell(qrCodeImage);
			companyNameCell13.setHorizontalAlignment( Element.ALIGN_CENTER);
			companyNameCell13.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell13.setRowspan(3);
			companyNameCell13.setColspan(2);
			coilDetailsTab.addCell(companyNameCell13);		

			Paragraph netwtParagraph = new Paragraph();
			netwtParagraph.add(new Phrase(new Chunk("Net Wt(kgs):  ", font8b)));
			netwtParagraph.add(new Phrase(new Chunk(resp.getFweight(), font11b)));
			PdfPCell companyNameCell10 = new PdfPCell(netwtParagraph);
			companyNameCell10.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell10.setColspan(2);
			companyNameCell10.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell10.setFixedHeight(23);
			coilDetailsTab.addCell(companyNameCell10);			

			Paragraph coilbatchParagraph = new Paragraph();
			coilbatchParagraph.add(new Phrase(new Chunk("COIL BATCH ID:  ", font8b)));
			coilbatchParagraph.add(new Phrase(new Chunk(resp.getCoilBatchNo(), font11b)));
			PdfPCell companyNameCell11 = new PdfPCell(coilbatchParagraph);
			companyNameCell11.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell11.setColspan(2);
			companyNameCell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell11.setFixedHeight(23);
			coilDetailsTab.addCell(companyNameCell11);			
			
			PdfPTable newCoilDetailsTab = new PdfPTable(3);
			newCoilDetailsTab.setWidthPercentage(100);
			newCoilDetailsTab.setWidths(new int[] {100, 80, 100});

			PdfPCell companyNameCell12 = new PdfPCell(new Phrase(new Chunk("MILL:  ", font7b)));
			companyNameCell12.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell12.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell12.setBorder( Rectangle.RIGHT);
			newCoilDetailsTab.addCell(companyNameCell12);	

			PdfPCell companyNameCell14 = new PdfPCell(new Phrase(new Chunk("INV NO:  "+resp.getCustomerInvoiceNo(), font7b)));
			companyNameCell14.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell14.setVerticalAlignment( Element.ALIGN_MIDDLE);
			companyNameCell14.setBorder( Rectangle.NO_BORDER);
			newCoilDetailsTab.addCell(companyNameCell14);		

			PdfPCell companyNameCell15 = new PdfPCell(new Phrase(new Chunk("TAG NO:  ", font7b)));
			companyNameCell15.setHorizontalAlignment( Element.ALIGN_LEFT);
			companyNameCell15.setBorder( Rectangle.LEFT);
			companyNameCell15.setVerticalAlignment( Element.ALIGN_MIDDLE);
			newCoilDetailsTab.addCell(companyNameCell15);		

			PdfPCell cell11 = new PdfPCell();
			cell11.setHorizontalAlignment( Element.ALIGN_LEFT);
			cell11.setColspan(4);
			cell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
			cell11.setFixedHeight(16);
			cell11.addElement(newCoilDetailsTab);
			coilDetailsTab.addCell(cell11);			

			document.add( coilDetailsTab );
			document.close();
			System.out.println("inward Label Print generated successfully..!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return labelFile;
	}
	
	public File renderWIPLabelPrintPDF(LabelPrintDTO labelPrintDTO, File file) throws IOException, DocumentException {
		logger.info("renderWIPLabelPrintPDF ");
		Document document = null;
		int tableRowHeight = 20;
		try {
			List<QRCodeResponse> respList = fetchLabelData(2, labelPrintDTO);
			
			if(respList!=null && respList.size()>0) {
				document = new Document();
				Rectangle myPagesize = new Rectangle (284, 213);
				FileOutputStream fos = new FileOutputStream(file);
				document = new Document(myPagesize, 2f, 2f, 3f, 2f);
	
				PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
				pdfWriter.setBoxSize("art", myPagesize);
				
				Font font5 = FontFactory.getFont(BaseFont.WINANSI, 5f);
				Font font7b = FontFactory.getFont(BaseFont.WINANSI, 7f, Font.BOLD);
				Font font8b = FontFactory.getFont(BaseFont.WINANSI, 8f, Font.BOLD);
				Font font11b = FontFactory.getFont(BaseFont.WINANSI, 10f, Font.BOLD);
				Font font11u = FontFactory.getFont(BaseFont.WINANSI, 11f, Font.UNDERLINE | Font.BOLD);
				Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);
				document.open();
	
				for (QRCodeResponse response : respList) {
	
					document.newPage();
					PdfPTable coilDetailsTab = new PdfPTable(4);
					coilDetailsTab.setWidthPercentage(100);
					coilDetailsTab.setWidths(new int[] {40, 100, 70, 70});
	
					PdfPCell companyNameCell = new PdfPCell(new Phrase("ASPEN STEEL PVT LTD", font11u));
					companyNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
					companyNameCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
					companyNameCell.setColspan(4);
					coilDetailsTab.addCell(companyNameCell);	
	
					PdfPCell addressCell2 = new PdfPCell(new Phrase("Email : aspen.bidadi@gmail.com", font5));
					addressCell2.setHorizontalAlignment( Element.ALIGN_CENTER);
					addressCell2.setVerticalAlignment( Element.ALIGN_TOP);
					addressCell2.setFixedHeight(9);
					addressCell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					addressCell2.setColspan(4);
					coilDetailsTab.addCell(addressCell2);
	
					PdfPTable unitDetailsTab = new PdfPTable(3);
					unitDetailsTab.setWidthPercentage(100);
					unitDetailsTab.setWidths(new int[] {90, 90, 90});
	
					PdfPCell unit1Cell = new PdfPCell(new Phrase(new Chunk("UNIT - I", font7b)));
					unit1Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit1Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit1Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit1Cell);	
	
					PdfPCell unit2Cell = new PdfPCell(new Phrase(new Chunk("UNIT - II", font7b)));
					unit2Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit2Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit2Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit2Cell);	
	
					PdfPCell unit3Cell = new PdfPCell(new Phrase(new Chunk("UNIT - III", font7b)));
					unit3Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit3Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit3Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit3Cell);	
	
					PdfPCell addressCELL = new PdfPCell();
					addressCELL.setHorizontalAlignment(Element.ALIGN_CENTER);
					addressCELL.setColspan(4);
					addressCELL.setVerticalAlignment(Element.ALIGN_MIDDLE);
					addressCELL.setFixedHeight(15);
					addressCELL.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					addressCELL.addElement(unitDetailsTab);
					coilDetailsTab.addCell(addressCELL);
	
					PdfPCell companyNameCell1 = new PdfPCell(new Phrase(new Chunk("WIP", font12b)));			
					companyNameCell1.setHorizontalAlignment( Element.ALIGN_CENTER);
					companyNameCell1.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell1.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell1);	
					
					Paragraph custNameParagraph = new Paragraph();
					custNameParagraph.add(new Phrase(new Chunk("CUST NAME: ", font8b)));
					custNameParagraph.add(new Phrase(new Chunk(response.getPartyName().toUpperCase(), font11b)));
					PdfPCell companyNameCell2 = new PdfPCell(custNameParagraph);
					companyNameCell2.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell2.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell2.setFixedHeight(tableRowHeight);
					companyNameCell2.setColspan(2);
					coilDetailsTab.addCell(companyNameCell2);	
	
					Paragraph dateParagraph = new Paragraph();
					dateParagraph.add(new Phrase(new Chunk("Process Dt:  ", font8b)));
					dateParagraph.add(new Phrase(new Chunk(""+response.getInstructionDate(), font11b)));	
					PdfPCell companyNameCell8 = new PdfPCell(dateParagraph);
					companyNameCell8.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell8.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell8.setFixedHeight(24);
					coilDetailsTab.addCell(companyNameCell8);	
	
					Paragraph coilParagraph = new Paragraph();
					coilParagraph.add(new Phrase(new Chunk("ASPL COIL NO: ", font8b)));
					coilParagraph.add(new Phrase(new Chunk(response.getCoilNo(), font11b)));
					PdfPCell companyNameCell3 = new PdfPCell(coilParagraph);
					companyNameCell3.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell3.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell3.setColspan(2);
					companyNameCell3.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell3);	
	
					Paragraph specParagraph = new Paragraph();
					specParagraph.add(new Phrase(new Chunk("SPEC: ", font8b)));
					specParagraph.add(new Phrase(new Chunk(response.getMaterialDesc(), font11b)));
					PdfPCell companyNameCell4 = new PdfPCell(specParagraph);
					companyNameCell4.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell4.setColspan(2);
					companyNameCell4.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell4.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell4);		
	
					Paragraph mcoilParagraph = new Paragraph();
					mcoilParagraph.add(new Phrase(new Chunk("CUST COIL NO: ", font8b)));
					mcoilParagraph.add(new Phrase(new Chunk(response.getCustomerBatchNo(), font11b)));			
					PdfPCell companyNameCell5 = new PdfPCell(mcoilParagraph);
					companyNameCell5.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell5.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell5.setColspan(2);
					companyNameCell5.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell5);	
	
					Paragraph gradeParagraph = new Paragraph();
					gradeParagraph.add(new Phrase(new Chunk("GRADE: ", font8b)));
					gradeParagraph.add(new Phrase(new Chunk(response.getMaterialGrade(), font11b)));	
					PdfPCell companyNameCell6 = new PdfPCell(gradeParagraph);
					companyNameCell6.setColspan(2);
					companyNameCell6.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell6.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell6.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell6);		
					PdfPCell companyNameCell7 = null;
					if(response.getProcessId() == 1 || response.getProcessId() == 3 ) {
						companyNameCell7 = new PdfPCell(new Phrase("T: "+response.getFthickness()+"        "+"W: "+response.getFwidth()+"       "+"L: "+response.getFlength()+"    Qty : "+response.getPlannedNoOfPieces(), font11b));
					} else {
						companyNameCell7 = new PdfPCell(new Phrase("T: "+response.getFthickness()+"        "+"W: "+response.getFwidth()+"       "+"L: "+response.getFlength(), font11b));
					}
					companyNameCell7.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell7.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell7.setColspan(4);
					companyNameCell7.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell7);		
					
					Paragraph grosswtParagraph = new Paragraph();
					grosswtParagraph.add(new Phrase(new Chunk("Gross Wt(kgs):  ", font8b)));
					grosswtParagraph.add(new Phrase(new Chunk(response.getFweight(), font11b)));
					PdfPCell companyNameCell9 = new PdfPCell(grosswtParagraph);
					companyNameCell9.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell9.setColspan(2);
					companyNameCell9.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell9.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell9);	
					
					Image qrCodeImage = Image.getInstance(getQRCodePlan(response));
					PdfPCell companyNameCell13 = new PdfPCell(qrCodeImage);
					companyNameCell13.setHorizontalAlignment( Element.ALIGN_CENTER);
					companyNameCell13.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell13.setRowspan(3);
					companyNameCell13.setColspan(2);
					coilDetailsTab.addCell(companyNameCell13);		
	
					Paragraph netwtParagraph = new Paragraph();
					netwtParagraph.add(new Phrase(new Chunk("Net Wt(kgs):  ", font8b)));
					netwtParagraph.add(new Phrase(new Chunk(response.getFweight(), font11b)));
					PdfPCell companyNameCell10 = new PdfPCell(netwtParagraph);
					companyNameCell10.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell10.setColspan(2);
					companyNameCell10.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell10.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell10);			
	
					Paragraph coilbatchParagraph = new Paragraph();
					coilbatchParagraph.add(new Phrase(new Chunk("END USER:  ", font8b)));
					coilbatchParagraph.add(new Phrase(new Chunk(response.getEndUserTag(), font11b)));
					PdfPCell companyNameCell11 = new PdfPCell(coilbatchParagraph);
					companyNameCell11.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell11.setColspan(2);
					companyNameCell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell11.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell11);			
					
					PdfPTable newCoilDetailsTab = new PdfPTable(3);
					newCoilDetailsTab.setWidthPercentage(100);
					newCoilDetailsTab.setWidths(new int[] {100, 80, 100});
	
					PdfPCell companyNameCell12 = new PdfPCell(new Phrase(new Chunk("Batch No:  "+response.getCustomerBatchNo(), font7b)));
					companyNameCell12.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell12.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell12.setBorder( Rectangle.RIGHT);
					newCoilDetailsTab.addCell(companyNameCell12);	
	
					PdfPCell companyNameCell14 = new PdfPCell(new Phrase(new Chunk("SLIT NO:  ", font7b)));
					companyNameCell14.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell14.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell14.setBorder( Rectangle.NO_BORDER);
					newCoilDetailsTab.addCell(companyNameCell14);		
	
					PdfPCell companyNameCell15 = new PdfPCell(new Phrase(new Chunk("PKT ID:  "+response.getInstructionId(), font7b)));
					companyNameCell15.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell15.setBorder( Rectangle.LEFT);
					companyNameCell15.setVerticalAlignment( Element.ALIGN_MIDDLE);
					newCoilDetailsTab.addCell(companyNameCell15);		
	
					PdfPCell cell11 = new PdfPCell();
					cell11.setHorizontalAlignment( Element.ALIGN_LEFT);
					cell11.setColspan(4);
					cell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
					cell11.setFixedHeight(16);
					cell11.addElement(newCoilDetailsTab);
					coilDetailsTab.addCell(cell11);			
					document.add( coilDetailsTab );
				}
				document.close();
			}
			logger.info("WIP Label Print generated successfully..!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return file;
	}

	private byte[] getQRCodeInward(QRCodeResponse resp) {
		byte[] pngData = null;

		try {
			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + resp.getCoilNo());
			text.append("\nCustomer BatchNo : " + resp.getCustomerBatchNo());
			text.append("\nMaterial Type : " + resp.getMaterialDesc());
			text.append("\nMaterial Grade : " + resp.getMaterialGrade());
			text.append("\nThickness : " + resp.getFthickness());
			text.append("\nWidth : " + resp.getFwidth());
			text.append("\nNet Weight : " + resp.getFweight());
			text.append("\nGross Weight : " + resp.getGrossWeight());
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 60, 67);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageConfig con = new MatrixToImageConfig();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
			pngData = pngOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pngData;
	}
	
	private byte[] getQRCodePlan(QRCodeResponse resp) {
		byte[] pngData = null;

		try {

			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + resp.getCoilNo());
			text.append("\nPacket Id : " + resp.getInstructionId());
			text.append("\nCustomer BatchNo : " + resp.getCustomerBatchNo());
			text.append("\nCustomer Name : " + resp.getPartyName());
			text.append("\nMaterial Description : " + resp.getMaterialDesc());
			text.append("\nMaterial Grade : " + resp.getMaterialGrade());
			text.append("\nT * W * L : " + resp.getFthickness() + " * " + resp.getFwidth() + " * " + resp.getFlength());
			text.append("\nNet Weight : " + resp.getFweight());
			text.append("\nEnd User Tag : " + resp.getEndUserTag());

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 60, 67);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageConfig con = new MatrixToImageConfig();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
			pngData = pngOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pngData;
	}
	
	private byte[] getQRCodePlanFG(QRCodeResponse resp) {
		byte[] pngData = null;

		try {

			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + resp.getCoilNo());
			text.append("\nPacket Id : " + resp.getInstructionId());
			text.append("\nCustomer BatchNo : " + resp.getCustomerBatchNo());
			text.append("\nCustomer Name : " + resp.getPartyName());
			text.append("\nMaterial Description : " + resp.getMaterialDesc());
			text.append("\nMaterial Grade : " + resp.getMaterialGrade());
			text.append("\nT * W * L : " + resp.getFthickness() + " * " + resp.getActualwidth() + " * " + resp.getActuallength());
			text.append("\nNet Weight : " + resp.getActualweight());
			text.append("\nEnd User Tag : " + resp.getEndUserTag());

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 60, 67);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageConfig con = new MatrixToImageConfig();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
			pngData = pngOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pngData;
	}
	
	public List<QRCodeResponse> fetchLabelData(Integer stts, LabelPrintDTO labelPrintDTO) {
		List<Object[]> packetsList = null;

		if(stts == 2) {
			packetsList = partDetailsRepository.wipLabelData(stts, labelPrintDTO.getPartDetailsId());
		} else {
			packetsList = partDetailsRepository.fgLabelData(stts, labelPrintDTO.getPartDetailsId());
		}
		
		List<QRCodeResponse> qirList = new ArrayList<QRCodeResponse>();
		for (Object[] result : packetsList) {
			QRCodeResponse resp = new QRCodeResponse();
			resp.setCoilNo(result[0] != null ? (String) result[0] : "");
			resp.setCustomerBatchNo(result[1] != null ? (String) result[1] : "");
			resp.setPartyName(result[2] != null ? (String) result[2] : "");
			resp.setInstructionDate(result[3] != null ? (String) result[3] : "");
			resp.setMaterialDesc(result[4] != null ? (String) result[4] : "");
			resp.setMaterialGrade(result[5] != null ? (String) result[5] : "");
			resp.setInstructionId(result[6] != null ? (Integer) result[6] : 0);

			Double fThickness = result[7] != null ? (Float) result[7] : 0.00;
			Double fWidth = result[8] != null ? (Float) result[8] : 0.00;
			Double fLength = result[9] != null ? (Float) result[9] : 0.00;
			Double fweight = result[10] != null ? (Float) result[10] : 0.00;
			Double actualWidth = result[11] != null ? (Float) result[11] : 0.00;
			Double actualLength = result[12] != null ? (Float) result[12] : 0.00;
			Double actualWeight = result[13] != null ? (Float) result[13] : 0.00;
			resp.setFthickness((decfor.format(fThickness.doubleValue())));
			resp.setFwidth((decfor.format(fWidth.doubleValue())));
			resp.setFlength((decfor.format(fLength.doubleValue())));
			resp.setFweight((decfor.format(fweight.doubleValue())));
			resp.setActualwidth((decfor.format(actualWidth.doubleValue())));
			resp.setActuallength((decfor.format(actualLength.doubleValue())));
			resp.setActualweight((decfor.format(actualWeight.doubleValue())));
			resp.setEndUserTag(result[14] != null ? (String) result[14] : "");
			resp.setPlannedNoOfPieces(result[15] != null ? (Integer) result[15] : 0);
			resp.setMotherCoilNo(result[16] != null ? (String) result[16] : "");
			resp.setIsSlitAndCut(result[17] != null ? (Boolean) result[17] : false);
			resp.setProcessId( result[18] != null ? (Integer) result[18] : 0);
			qirList.add(resp);
		}
		return qirList;
	}

	public File renderFGLabelPrintPDF(LabelPrintDTO labelPrintDTO, InstructionFinishDto instructionFinishDto, File file)
			throws IOException, DocumentException {
		logger.info("renderFGLabelPrintPDF Started == ");
		Document document = null;
		int tableRowHeight = 20;
		try {
			
			List<QRCodeResponse> respList = fetchLabelData(3, labelPrintDTO);
			if(respList!=null && respList.size()>0) {
				document = new Document();
				Rectangle myPagesize = new Rectangle (284, 213);
				FileOutputStream fos = new FileOutputStream(file);
				document = new Document(myPagesize, 2f, 2f, 3f, 2f);
	
				PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
				pdfWriter.setBoxSize("art", myPagesize);
				
				Font font5 = FontFactory.getFont(BaseFont.WINANSI, 5f);
				Font font7b = FontFactory.getFont(BaseFont.WINANSI, 7f, Font.BOLD);
				Font font8b = FontFactory.getFont(BaseFont.WINANSI, 8f, Font.BOLD);
				Font font11b = FontFactory.getFont(BaseFont.WINANSI, 10f, Font.BOLD);
				Font font11u = FontFactory.getFont(BaseFont.WINANSI, 11f, Font.UNDERLINE | Font.BOLD);
				Font font12b = FontFactory.getFont(BaseFont.WINANSI, 12f, Font.BOLD);
				document.open();
	
				for (QRCodeResponse response : respList) {
	
					document.newPage();
					PdfPTable coilDetailsTab = new PdfPTable(4);
					coilDetailsTab.setWidthPercentage(100);
					coilDetailsTab.setWidths(new int[] {40, 100, 70, 70});
	
					PdfPCell companyNameCell = new PdfPCell(new Phrase("ASPEN STEEL PVT LTD", font11u));
					companyNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
					companyNameCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
					companyNameCell.setColspan(4);
					coilDetailsTab.addCell(companyNameCell);	
	
					PdfPCell addressCell2 = new PdfPCell(new Phrase("Email : aspen.bidadi@gmail.com", font5));
					addressCell2.setHorizontalAlignment( Element.ALIGN_CENTER);
					addressCell2.setVerticalAlignment( Element.ALIGN_TOP);
					addressCell2.setFixedHeight(9);
					addressCell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					addressCell2.setColspan(4);
					coilDetailsTab.addCell(addressCell2);
	
					PdfPTable unitDetailsTab = new PdfPTable(3);
					unitDetailsTab.setWidthPercentage(100);
					unitDetailsTab.setWidths(new int[] {90, 90, 90});
	
					PdfPCell unit1Cell = new PdfPCell(new Phrase(new Chunk("UNIT - I", font7b)));
					unit1Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit1Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit1Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit1Cell);	
	
					PdfPCell unit2Cell = new PdfPCell(new Phrase(new Chunk("UNIT - II", font7b)));
					unit2Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit2Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit2Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit2Cell);	
	
					PdfPCell unit3Cell = new PdfPCell(new Phrase(new Chunk("UNIT - III", font7b)));
					unit3Cell.setHorizontalAlignment( Element.ALIGN_CENTER);
					unit3Cell.setVerticalAlignment( Element.ALIGN_TOP);
					unit3Cell.setBorder( Rectangle.NO_BORDER);
					unitDetailsTab.addCell(unit3Cell);	
	
					PdfPCell addressCELL = new PdfPCell();
					addressCELL.setHorizontalAlignment(Element.ALIGN_CENTER);
					addressCELL.setColspan(4);
					addressCELL.setVerticalAlignment(Element.ALIGN_MIDDLE);
					addressCELL.setFixedHeight(15);
					addressCELL.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					addressCELL.addElement(unitDetailsTab);
					coilDetailsTab.addCell(addressCELL);
	
					PdfPCell companyNameCell1 = new PdfPCell(new Phrase(new Chunk("FG", font12b)));			
					companyNameCell1.setHorizontalAlignment( Element.ALIGN_CENTER);
					companyNameCell1.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell1.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell1);	
					
					Paragraph custNameParagraph = new Paragraph();
					custNameParagraph.add(new Phrase(new Chunk("CUST NAME: ", font8b)));
					custNameParagraph.add(new Phrase(new Chunk(response.getPartyName().toUpperCase(), font11b)));
					PdfPCell companyNameCell2 = new PdfPCell(custNameParagraph);
					companyNameCell2.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell2.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell2.setFixedHeight(tableRowHeight);
					companyNameCell2.setColspan(2);
					coilDetailsTab.addCell(companyNameCell2);	
	
					Paragraph dateParagraph = new Paragraph();
					dateParagraph.add(new Phrase(new Chunk("Process Dt:  ", font8b)));
					dateParagraph.add(new Phrase(new Chunk(""+response.getInstructionDate(), font11b)));	
					PdfPCell companyNameCell8 = new PdfPCell(dateParagraph);
					companyNameCell8.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell8.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell8.setFixedHeight(24);
					coilDetailsTab.addCell(companyNameCell8);	
	
					Paragraph coilParagraph = new Paragraph();
					coilParagraph.add(new Phrase(new Chunk("ASPL COIL NO: ", font8b)));
					coilParagraph.add(new Phrase(new Chunk(response.getCoilNo(), font11b)));
					PdfPCell companyNameCell3 = new PdfPCell(coilParagraph);
					companyNameCell3.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell3.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell3.setColspan(2);
					companyNameCell3.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell3);	
	
					Paragraph specParagraph = new Paragraph();
					specParagraph.add(new Phrase(new Chunk("SPEC: ", font8b)));
					specParagraph.add(new Phrase(new Chunk(response.getMaterialDesc(), font11b)));
					PdfPCell companyNameCell4 = new PdfPCell(specParagraph);
					companyNameCell4.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell4.setColspan(2);
					companyNameCell4.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell4.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell4);		
	
					Paragraph mcoilParagraph = new Paragraph();
					mcoilParagraph.add(new Phrase(new Chunk("CUST COIL NO: ", font8b)));
					mcoilParagraph.add(new Phrase(new Chunk(response.getCustomerBatchNo(), font11b)));			
					PdfPCell companyNameCell5 = new PdfPCell(mcoilParagraph);
					companyNameCell5.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell5.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell5.setColspan(2);
					companyNameCell5.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell5);	
	
					Paragraph gradeParagraph = new Paragraph();
					gradeParagraph.add(new Phrase(new Chunk("GRADE: ", font8b)));
					gradeParagraph.add(new Phrase(new Chunk(response.getMaterialGrade(), font11b)));	
					PdfPCell companyNameCell6 = new PdfPCell(gradeParagraph);
					companyNameCell6.setColspan(2);
					companyNameCell6.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell6.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell6.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell6);		
					PdfPCell companyNameCell7 = null;
					if(response.getProcessId() == 1 || response.getProcessId() == 3 ) {
						companyNameCell7 = new PdfPCell(new Phrase("T: "+response.getFthickness()+"        "+"W: "+response.getActualwidth()+"       "+"L: "+response.getActuallength()+"    Qty : "+response.getPlannedNoOfPieces(), font11b));
					} else {
						companyNameCell7 = new PdfPCell(new Phrase("T: "+response.getFthickness()+"        "+"W: "+response.getActualwidth()+"       "+"L: "+response.getActuallength(), font11b));
					}
					companyNameCell7.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell7.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell7.setColspan(4);
					companyNameCell7.setFixedHeight(tableRowHeight);
					coilDetailsTab.addCell(companyNameCell7);		
					
					Paragraph grosswtParagraph = new Paragraph();
					grosswtParagraph.add(new Phrase(new Chunk("Gross Wt(kgs):  ", font8b)));
					grosswtParagraph.add(new Phrase(new Chunk("", font11b)));
					PdfPCell companyNameCell9 = new PdfPCell(grosswtParagraph);
					companyNameCell9.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell9.setColspan(2);
					companyNameCell9.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell9.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell9);	
					
					Image qrCodeImage = Image.getInstance(getQRCodePlanFG(response));
					PdfPCell companyNameCell13 = new PdfPCell(qrCodeImage);
					companyNameCell13.setHorizontalAlignment( Element.ALIGN_CENTER);
					companyNameCell13.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell13.setRowspan(3);
					companyNameCell13.setColspan(2);
					coilDetailsTab.addCell(companyNameCell13);		
	
					Paragraph netwtParagraph = new Paragraph();
					netwtParagraph.add(new Phrase(new Chunk("Net Wt(kgs):  ", font8b)));
					netwtParagraph.add(new Phrase(new Chunk(response.getActualweight(), font11b)));
					PdfPCell companyNameCell10 = new PdfPCell(netwtParagraph);
					companyNameCell10.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell10.setColspan(2);
					companyNameCell10.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell10.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell10);			
	
					Paragraph coilbatchParagraph = new Paragraph();
					coilbatchParagraph.add(new Phrase(new Chunk("END USER:  ", font8b)));
					coilbatchParagraph.add(new Phrase(new Chunk(response.getEndUserTag(), font11b)));
					PdfPCell companyNameCell11 = new PdfPCell(coilbatchParagraph);
					companyNameCell11.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell11.setColspan(2);
					companyNameCell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell11.setFixedHeight(23);
					coilDetailsTab.addCell(companyNameCell11);			
					
					PdfPTable newCoilDetailsTab = new PdfPTable(3);
					newCoilDetailsTab.setWidthPercentage(100);
					newCoilDetailsTab.setWidths(new int[] {100, 80, 100});
	
					PdfPCell companyNameCell12 = new PdfPCell(new Phrase(new Chunk("Batch No:  "+response.getCustomerBatchNo(), font7b)));
					companyNameCell12.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell12.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell12.setBorder( Rectangle.RIGHT);
					newCoilDetailsTab.addCell(companyNameCell12);	
	
					PdfPCell companyNameCell14 = new PdfPCell(new Phrase(new Chunk("SLIT NO:  ", font7b)));
					companyNameCell14.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell14.setVerticalAlignment( Element.ALIGN_MIDDLE);
					companyNameCell14.setBorder( Rectangle.NO_BORDER);
					newCoilDetailsTab.addCell(companyNameCell14);		
	
					PdfPCell companyNameCell15 = new PdfPCell(new Phrase(new Chunk("PKT ID:  "+response.getInstructionId(), font7b)));
					companyNameCell15.setHorizontalAlignment( Element.ALIGN_LEFT);
					companyNameCell15.setBorder( Rectangle.LEFT);
					companyNameCell15.setVerticalAlignment( Element.ALIGN_MIDDLE);
					newCoilDetailsTab.addCell(companyNameCell15);		
	
					PdfPCell cell11 = new PdfPCell();
					cell11.setHorizontalAlignment( Element.ALIGN_LEFT);
					cell11.setColspan(4);
					cell11.setVerticalAlignment( Element.ALIGN_MIDDLE);
					cell11.setFixedHeight(16);
					cell11.addElement(newCoilDetailsTab);
					coilDetailsTab.addCell(cell11);			
					document.add( coilDetailsTab );
				}
				document.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("error == "+ex.getMessage());
		}
		file.deleteOnExit();
		return file;
	}

 
}
