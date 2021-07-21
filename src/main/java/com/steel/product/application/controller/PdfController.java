package com.steel.product.application.controller;

import com.lowagie.text.DocumentException;
import com.steel.product.application.service.AddressService;
import com.steel.product.application.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class PdfController {
    private AddressService addressService;
    private PdfService pdfService;

    @Autowired
    public PdfController(AddressService addressService, PdfService pdfService) {
        this.addressService = addressService;
        this.pdfService = pdfService;
    }

    @GetMapping("/download-pdf")
    public void downloadPDFResource(HttpServletResponse response) {
        try {
            Path file = Paths.get(pdfService.generatePdf().getAbsolutePath());
            if (Files.exists(file)) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition",
                        "attachment; filename=" + file.getFileName());
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
        }
    }
}
