package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.DeliveryPdfDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.dto.pdf.PdfResponseDto;
import com.steel.product.application.service.AddressService;
import com.steel.product.application.service.PdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@CrossOrigin
@RequestMapping("/api/pdf")
public class PdfController {
    private AddressService addressService;
    private PdfService pdfService;
    private Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    public PdfController(AddressService addressService, PdfService pdfService) {
        this.addressService = addressService;
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
            file = Paths.get(pdfService.generateDeliveryPdf(deliveryPdfDto).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();
        return new ResponseEntity<>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }

    @GetMapping("/slit/{partDetailsId}")
    public ResponseEntity<PdfResponseDto> downloadSlitPDF(@PathVariable("partDetailsId") String partDetailsId) {
        Path file = null;
        byte[] bytes = null;
        StringBuilder builder = new StringBuilder();
        try {
            file = Paths.get(pdfService.generatePdf(partDetailsId).getAbsolutePath());
            bytes = Files.readAllBytes(file);
            builder.append(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
        String encodedFile = builder.toString();

        return new ResponseEntity<PdfResponseDto>(new PdfResponseDto(encodedFile), HttpStatus.OK);
    }


}
