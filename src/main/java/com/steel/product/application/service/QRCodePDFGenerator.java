package com.steel.product.application.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.steel.product.application.dto.pdf.PartDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.dto.qrcode.QRCodeResponse;

@Component
public class QRCodePDFGenerator {
	
	private final static Logger logger = LoggerFactory.getLogger("QRCodePDFGenerator");

	@Autowired PdfService pdfService;

	public byte[] getQRCode(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageConfig con = new MatrixToImageConfig();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
		byte[] pngData = pngOutputStream.toByteArray();

		return pngData;
	}

	public InputStreamResource inputStreamResource(byte[] pngData, int inwardEntryId )
			throws DocumentException, MalformedURLException, IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
		Paragraph para = new Paragraph("Scan Inward Details", font);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		document.add(Chunk.NEWLINE);

		Image image = Image.getInstance(pngData);
		image.scaleAbsolute(170f, 170f);
		image.setAlignment(Element.ALIGN_CENTER);

		document.add(image);
		document.close();

		try {
			pdfService.renderQRCodePdfInstruction("qrcode_inward"+"_"+inwardEntryId, String.valueOf(inwardEntryId), "QRCODE_INWARD_PDF", out.toByteArray());
		} catch (Exception e) {
			logger.error("error while uploading teh QRCODE INWARD == "+inwardEntryId);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
		return new InputStreamResource(bis);
	}
	
	public InputStreamResource planInputStreamResource( List<QRCodeResponse> instructionList, PartDto partDto)
			throws DocumentException, MalformedURLException, IOException, WriterException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		for (QRCodeResponse entry : instructionList) {
			document.newPage();
			byte[] pngData;
			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + entry.getCoilNo());
			text.append("\nPacket Id : " + entry.getInstructionId() );
			text.append("\nCustomer BatchNo : " + entry.getCustomerBatchNo());
			text.append("\nCustomer Name : " + entry.getPartyName() );
			text.append("\nMaterial Description : " + entry.getMaterialDesc());
			text.append("\nMaterial Grade : " + entry.getMaterialGrade());
			text.append("\nT * W * L : " + entry.getFthickness() +" * "+entry.getFwidth() +" * "+entry.getFlength() );
			text.append("\nNet Weight : " + entry.getNetWeight());
			text.append("\nEnd User Tag : " + entry.getGrossWeight());
			pngData = getQRCode(text.toString(), 0, 0);

			Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
			Paragraph para = new Paragraph("Scan Plan Details", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);
			document.add(Chunk.NEWLINE);

			Image image = Image.getInstance(pngData);
			image.scaleAbsolute(170f, 170f);
			image.setAlignment(Element.ALIGN_CENTER);

			document.add(image);
		}
		
		document.close();

		try {
			pdfService.renderQRCodePdfInstruction("qrcode_plan"+"_"+partDto.getPartDetailsId(), partDto.getPartDetailsId(), "QRCODE_PLAN_PDF", out.toByteArray());
		} catch (Exception e) {
			logger.error("error while uploading the QRCODE PLAN == "+partDto.getPartDetailsId());
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
		return new InputStreamResource(bis);
	}

	public InputStreamResource planInputStreamResource_Finish( List<QRCodeResponse> instructionList, PdfDto pdfDto)
			throws DocumentException, MalformedURLException, IOException, WriterException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();

		for (QRCodeResponse entry : instructionList) {
			document.newPage();
			byte[] pngData;
			StringBuilder text = new StringBuilder();
			text.append("Coil NO : " + entry.getCoilNo());
			text.append("\nPacket Id : " + entry.getInstructionId() );
			text.append("\nCustomer BatchNo : " + entry.getCustomerBatchNo());
			text.append("\nCustomer Name : " + entry.getPartyName() );
			text.append("\nMaterial Description : " + entry.getMaterialDesc());
			text.append("\nMaterial Grade : " + entry.getMaterialGrade());
			text.append("\nT * W * L : " + entry.getFthickness() +" * "+entry.getFwidth() +" * "+entry.getFlength() );
			text.append("\nNet Weight : " + entry.getNetWeight());
			text.append("\nEnd User Tag : " + entry.getGrossWeight());
			pngData = getQRCode(text.toString(), 0, 0);

			Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
			Paragraph para = new Paragraph("Scan Plan Details", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);
			document.add(Chunk.NEWLINE);

			Image image = Image.getInstance(pngData);
			image.scaleAbsolute(170f, 170f);
			image.setAlignment(Element.ALIGN_CENTER);

			document.add(image);
		}
		
		document.close();

		try {
			pdfService.renderQRCodePdfInstruction("edit_finish_qrcode_plan"+"_"+pdfDto.getInwardId(), String.valueOf(pdfDto.getInwardId()), "QRCODE_EDITFINISH_PDF", out.toByteArray());
		} catch (Exception e) {
			logger.error("error while uploading the edit_finish_qrcode_plan == "+pdfDto.getInwardId());
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
		return new InputStreamResource(bis);
	}
}
