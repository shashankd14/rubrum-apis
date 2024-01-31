package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.pdf.DeliveryPdfDto;
import com.steel.product.application.dto.pdf.PartDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.service.PdfService;
import com.steel.product.application.service.QualityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@CrossOrigin
@RequestMapping("/pdf")
public class PdfController {
	
	private final static Logger logger = LoggerFactory.getLogger("PdfController");
	
    private QualityService qualityService;

    private PdfService pdfService;

    @Autowired
    public PdfController(QualityService qualityService, PdfService pdfService) {
        this.qualityService = qualityService;
        this.pdfService = pdfService;
    }

    @PostMapping("/inward")
    public ResponseEntity<PdfResponseDto> downloadInwardPDF(@RequestBody PdfDto pdfDto, HttpServletResponse response) {
        Path file = null;
        byte[] bytes = null;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(pdfService.generatePdf(pdfDto).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }

    @PostMapping("/delivery")
    public ResponseEntity<PdfResponseDto> downloadDeliveryPDF(@RequestBody DeliveryPdfDto deliveryPdfDto, HttpServletResponse response) {
        Path file = null;
        byte[] bytes = null;
        StringBuilder builder = new StringBuilder();
        try {
        	
        	if(!(deliveryPdfDto.getLaminationId() !=null && deliveryPdfDto.getLaminationId() > 0 )) {
        		deliveryPdfDto.setLaminationId(0);
			}
        	
            file = Paths.get(pdfService.generateDeliveryPdf(deliveryPdfDto).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
            
            /*File fileaa = new File("D:/newfile.pdf");
            FileOutputStream fos = new FileOutputStream(fileaa);
            fos.write(bytes);
            System.out.println("PDF File Saved");*/
            
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();
        return new ResponseEntity<>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PdfResponseDto> downloadPDF(@RequestBody PartDto partDto) {
        Path file;
        byte[] bytes;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(pdfService.generatePdf(partDto).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }

	@PostMapping("/qirpdf/{qirId}")
	public ResponseEntity<PdfResponseDto> qirpdf(@PathVariable("qirId") Integer qirId) {
        Path file;
        byte[] bytes;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(qualityService.qirPDF(qirId).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
	}

	
	@PostMapping("/labelprint/fg")
	public ResponseEntity<PdfResponseDto> labelPrint(@RequestBody InstructionFinishDto instructionFinishDto) {
        Path file;
        byte[] bytes;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(pdfService.labelPrint(instructionFinishDto).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
	}

}
