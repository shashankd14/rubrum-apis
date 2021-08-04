package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.DeliveryPdfDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.service.AddressService;
import com.steel.product.application.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
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
    public void downloadInwardPDF(@RequestBody PdfDto pdfDto, HttpServletResponse response) {
        try {

            Path file = Paths.get(pdfService.generatePdf(pdfDto).getAbsolutePath());
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
