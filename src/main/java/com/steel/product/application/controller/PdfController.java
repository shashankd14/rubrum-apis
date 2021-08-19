package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.DeliveryPdfDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.service.AddressService;
import com.steel.product.application.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
@RequestMapping("/api/pdf")
public class PdfController {
    private AddressService addressService;
    private PdfService pdfService;

    @Autowired
    public PdfController(AddressService addressService, PdfService pdfService) {
        this.addressService = addressService;
        this.pdfService = pdfService;
    }

    @PostMapping("/inward")
    public ResponseEntity<InputStreamResource> downloadInwardPDF(@RequestBody PdfDto pdfDto, HttpServletResponse response) {
        Path file = null;
        try {

            file = Paths.get(pdfService.generatePdf(pdfDto).getAbsolutePath());
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-disposition","inline;filename=" +file.getFileName());
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.toFile().length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(resource);
//            if (Files.exists(file)) {
//                response.setContentType("application/pdf");
//                response.addHeader("Content-Disposition",
//                        "inline; filename=" + file.getFileName());
//                Files.copy(file, response.getOutputStream());
//                response.getOutputStream().flush();
//            }

    }

    @PostMapping("/delivery")
    public void downloadDeliveryPDF(@RequestBody DeliveryPdfDto deliveryPdfDto, HttpServletResponse response) {
        try {

            Path file = Paths.get(pdfService.generateDeliveryPdf(deliveryPdfDto).getAbsolutePath());
            if (Files.exists(file)) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition",
                        "attachment; filename=" + file.getFileName());
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
            ex.printStackTrace();
        }
    }


}
