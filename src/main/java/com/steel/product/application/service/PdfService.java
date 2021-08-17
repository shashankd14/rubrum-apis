package com.steel.product.application.service;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dto.pdf.DeliveryChallanPdfDto;
import com.steel.product.application.dto.pdf.DeliveryPdfDto;
import com.steel.product.application.dto.pdf.InwardEntryPdfDto;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.entity.CompanyDetails;
import com.steel.product.application.entity.Instruction;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    private InwardEntryService inwardEntryService;
    private CompanyDetailsService companyDetailsService;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public PdfService(InwardEntryService inwardEntryService, CompanyDetailsService companyDetailsService, SpringTemplateEngine templateEngine) {
        this.inwardEntryService = inwardEntryService;
        this.companyDetailsService = companyDetailsService;
        this.templateEngine = templateEngine;
    }

    public File generatePdf(PdfDto pdfDto) throws IOException, org.dom4j.DocumentException, DocumentException {
        Context context = getContext(pdfDto);
        String html = loadAndFillTemplate(context,pdfDto.getProcessId());
        return renderPdf(html,"inward");
    }

    public File generateDeliveryPdf(DeliveryPdfDto deliveryPdfDto) throws IOException, org.dom4j.DocumentException, DocumentException {
        Context context = getDeliveryContext(deliveryPdfDto);
        String html = loadAndFillDeliveryTemplate(context,deliveryPdfDto);
        return renderPdf(html,"delivery");
    }

    private String loadAndFillDeliveryTemplate(Context context, DeliveryPdfDto deliveryPdfDto) {
        return templateEngine.process("DC-slit",context);
    }

    private Context getDeliveryContext(DeliveryPdfDto deliveryPdfDto) {
        Context context = new Context();
        List<InwardEntry> inwardEntries = inwardEntryService.findDeliveryItemsByInstructionIds(deliveryPdfDto.getInstructionIds());
        CompanyDetails companyDetails = companyDetailsService.findById(1);
        DeliveryChallanPdfDto deliveryChallanPdfDto = new DeliveryChallanPdfDto(companyDetails,inwardEntries);
        context.setVariable("deliveryChallan",deliveryChallanPdfDto);
        return context;
    }


    private File renderPdf(String html,String filename) throws IOException, DocumentException {
        File file = File.createTempFile("aspen-steel-"+filename, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource("/").getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private Context getContext(PdfDto pdfDto) {
        Context context = new Context();
        InwardEntry inwardEntry = inwardEntryService.getByEntryId(pdfDto.getInwardId());
        if(inwardEntry == null){
            throw new RuntimeException(String.format("InwardEntry with id %d does not have instructions with process id %d",
                    pdfDto.getInwardId(),pdfDto.getProcessId()));
        }
        if(pdfDto.getProcessId() != null) {
            inwardEntry.setInstruction(inwardEntry.getInstruction().stream()
                    .filter(ins -> ins.getProcess().getProcessId() == pdfDto.getProcessId()).collect(Collectors.toSet()));
        }
        InwardEntryPdfDto inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry);
        context.setVariable("inward", inwardEntryPdfDto);
        return context;
    }

    private String loadAndFillTemplate(Context context, Integer processId) {
        if(processId != null && processId == 1) {
            return templateEngine.process("Cutting-slip", context);
        }else if(processId != null && processId == 2) {
            return templateEngine.process("Slitting-slip", context);
        }else{
            return templateEngine.process("Inward",context);
        }
    }

}
