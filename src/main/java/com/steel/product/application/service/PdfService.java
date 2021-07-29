package com.steel.product.application.service;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PdfService {

    private static final String PDF_RESOURCES = "/pdf-resources/";
    private InwardEntryService inwardEntryService;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public PdfService(InwardEntryService inwardEntryService, SpringTemplateEngine templateEngine) {
        this.inwardEntryService = inwardEntryService;
        this.templateEngine = templateEngine;
    }

    public File generatePdf(PdfDto pdfDto, String process) throws IOException, DocumentException {
        Context context = getContext(pdfDto.getInwardId());
        String html = loadAndFillTemplate(context,process);
        return renderPdf(html);
    }


    private File renderPdf(String html) throws IOException, DocumentException {
        File file = File.createTempFile("inward-cut", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private Context getContext(Integer inwardId) {
        Context context = new Context();
        InwardEntry inwardEntry = inwardEntryService.getByEntryId(inwardId);
        context.setVariable("inward", inwardEntry);
        return context;
    }

    private String loadAndFillTemplate(Context context, String process) {
        if(process.equals("CUT")) {
            return templateEngine.process("Cutting-slip", context);
        }
        return templateEngine.process("Cutting-slip", context);
    }

}
