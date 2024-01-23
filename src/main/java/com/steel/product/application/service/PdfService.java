package com.steel.product.application.service;

import com.lowagie.text.DocumentException;
import com.steel.product.application.dao.FGLabelRepository;
import com.steel.product.application.dto.instruction.InstructionFinishDto;
import com.steel.product.application.dto.pdf.*;
import com.steel.product.application.dto.qrcode.QRCodeResponse;
import com.steel.product.application.entity.CompanyDetails;
import com.steel.product.application.entity.FGLabelEntity;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PdfService {

    private InwardEntryService inwardEntryService;
    private CompanyDetailsService companyDetailsService;
	private SpringTemplateEngine templateEngine;
	private InstructionService instructionService;
	private AWSS3Service awsS3Service;
	private PartDetailsService partDetailsService;
	private LabelPrintPDFGenerator labelPrintPDFGenerator;
	private FGLabelRepository fgLabelRepository;

	@Value("${aws.s3.bucketPDFs}")
	private String bucketName;

	@Autowired
	public PdfService(InwardEntryService inwardEntryService, CompanyDetailsService companyDetailsService,
			SpringTemplateEngine templateEngine, InstructionService instructionService, AWSS3Service awsS3Service,
			PartDetailsService partDetailsService, LabelPrintPDFGenerator labelPrintPDFGenerator,
			FGLabelRepository fgLabelRepository) {
		this.inwardEntryService = inwardEntryService;
		this.companyDetailsService = companyDetailsService;
		this.templateEngine = templateEngine;
		this.instructionService = instructionService;
		this.awsS3Service = awsS3Service;
		this.partDetailsService = partDetailsService;
		this.labelPrintPDFGenerator = labelPrintPDFGenerator;
		this.fgLabelRepository = fgLabelRepository;
	}

    public File generatePdf(PdfDto pdfDto) throws IOException, org.dom4j.DocumentException, DocumentException {
        Context context = getContext(pdfDto);
        String html = loadAndFillTemplate(context, pdfDto.getProcessId());
        return renderPdfInstruction(html, "inward", ""+pdfDto.getInwardId(), "INWARD_PDF");
    }

    public File generatePdf(PartDto partDto) throws IOException, org.dom4j.DocumentException, DocumentException {
        Context context = getContext(partDto);
        InwardEntryPdfDto inwardEntryPdfDto = (InwardEntryPdfDto)context.getVariable("inward");
        String html = loadAndFillTemplate(context,Integer.parseInt(inwardEntryPdfDto.getVProcess()));
        return renderPdfInstruction(html, "inward", partDto.getPartDetailsId(), "PLAN_PDF");
    }

    public File generateDeliveryPdf(DeliveryPdfDto deliveryPdfDto) throws IOException, org.dom4j.DocumentException, DocumentException {
        Context context = getDeliveryContext(deliveryPdfDto);
        String html = loadAndFillDeliveryTemplate(context, deliveryPdfDto);
        int deliverId=0;
        
        if(deliveryPdfDto.getInstructionIds()!=null && deliveryPdfDto.getInstructionIds().size()>0) {
            List<InwardEntry> inwardEntries = inwardEntryService.findDeliveryItemsByInstructionIds(deliveryPdfDto.getInstructionIds());
            InwardEntry inwardEntry =inwardEntries.get(0);
            for ( Instruction instruction : inwardEntry.getInstructions()) {
            	deliverId = instruction.getDeliveryDetails().getDeliveryId();
            }
        }
        
        return renderPdfInstruction(html, "delivery", ""+deliverId, "DC_PDF");
    }

    private String loadAndFillDeliveryTemplate(Context context, DeliveryPdfDto deliveryPdfDto) {
    	DeliveryChallanPdfDto deliveryChallanPdfDto = (DeliveryChallanPdfDto)context.getVariable("deliveryChallan");
        if("Y".equals(deliveryChallanPdfDto.getShowAmtDcPdfFlg())) {
            return templateEngine.process("DC-slit", context);
        } else {
            return templateEngine.process("DC-slit-without_price.html", context);
        }
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

    private File renderPdfInstruction(String html, String filename, String id, String processType) throws IOException, DocumentException {
        File file = File.createTempFile("aspen-steel-"+filename, ".pdf");
		File labelFile = File.createTempFile("labelprintInward_" +System.currentTimeMillis(), ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource("/").getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
    
		try {
			String fileUrl = "";
			if("INWARD_PDF".equals(processType)) {
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, "INWARD_"+id);
				instructionService.updateS3InwardPDF(Integer.parseInt(id), "INWARD_"+id);
			} else if("PLAN_PDF".equals(processType)) {
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, id);
				instructionService.updateS3PlanPDF(id, fileUrl);
			} else if("DC_PDF".equals(processType)) {
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, "DC_"+id);
				instructionService.updateS3DCPDF(Integer.parseInt(id), "DC_"+id);
			}
			System.out.println("fileUrl == "+fileUrl);
		} catch (Exception e) {
			System.out.println("Error while uploading pdf - " + e.getMessage());
		}
       
		// below code is for label print file save in S3
		try {
			LabelPrintDTO labelPrintDTO=new LabelPrintDTO();
			 
			if("INWARD_PDF".equals(processType)) {
				labelPrintDTO.setInwardEntryId(Integer.parseInt(id));
				labelPrintDTO.setProcess("inward");
				QRCodeResponse resp = inwardEntryService.getQRCodeDetails(labelPrintDTO.getInwardEntryId());
				labelFile = labelPrintPDFGenerator.renderInwardLabelPrintPDF(labelPrintDTO, resp, labelFile);
				file=labelFile;
				awsS3Service.uploadPDFFileToS3Bucket(bucketName, labelFile, "InwardLabel_"+id);
				inwardEntryService.updateS3InwardLabelPDF(Integer.parseInt(id), "InwardLabel_"+id);
			} else if ("PLAN_PDF".equalsIgnoreCase(processType)) {
				labelPrintDTO.setPartDetailsId(id);
				labelPrintDTO.setProcess("wip");
				labelFile = labelPrintPDFGenerator.renderWIPLabelPrintPDF(labelPrintDTO, labelFile);
				awsS3Service.uploadPDFFileToS3Bucket(bucketName, labelFile, "PlanLabel_"+id);
				instructionService.updateS3PlanLabelPDF(id, "PlanLabel_"+id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
        outputStream.close();
        file.deleteOnExit();
        labelFile.deleteOnExit();
        return file;
    }

    public File renderQRCodePdfInstruction(String filename, String id, String processType, byte[] byteArray) throws IOException {
        File file = File.createTempFile(filename, ".pdf");
    
		try {
			String fileUrl = "";
			if ("QRCODE_INWARD_PDF".equals(processType)) {
				OutputStream out = new FileOutputStream(file);
				out.write(byteArray);
				out.close();
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, "QRCODE_INWARD_" + id);
				inwardEntryService.updateQRCodeS3InwardPDF(id, "QRCODE_INWARD_" + id);
			} else if ("QRCODE_PLAN_PDF".equals(processType)) {
				OutputStream out = new FileOutputStream(file);
				out.write(byteArray);
				out.close();
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, "QRCODE_PLAN_" + id);
				partDetailsService.updatePartDetailsS3PDF(id, "QRCODE_PLAN_" + id);
			}  else if ("QRCODE_EDITFINISH_PDF".equals(processType)) {
				OutputStream out = new FileOutputStream(file);
				out.write(byteArray);
				out.close();
				fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, file, "QRCODE_EDITFINISH_" + id);
				inwardEntryService.updateQRCodeEditFinish(id, "QRCODE_EDITFINISH_" + id);
			} 
			System.out.println("fileUrl == "+fileUrl);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while uploading pdf - " + e.getMessage());
		}
        file.deleteOnExit();
        return file;
    }

    private Context getContext(PdfDto pdfDto) {
        Context context = new Context();
        Integer slitAndCutProcessId = 3;
        InwardEntry inwardEntry;
        List<InstructionResponsePdfDto> instructionResponsePdfDtos;
        List<InstructionResponsePdfDto> instructionsSlit = null;
        List<InstructionResponsePdfDto> instructionsCut = null;
        InwardEntryPdfDto inwardEntryPdfDto;
        if (pdfDto.getProcessId() != null && pdfDto.getProcessId().equals(slitAndCutProcessId)) {
            List<Instruction> instructions = instructionService.findSlitAndCutInstructionByInwardId(pdfDto.getInwardId());
            inwardEntry = instructions.get(0).getInwardId();
            instructionsCut = instructions.stream()
                    .filter(ins -> ins.getProcess().getProcessId() == 3)
                    .map(ins -> Instruction.valueOfInstructionPdf(ins, null))
                    .collect(Collectors.toList());
            instructionsSlit = instructions.stream()
                    .filter(ins -> ins.getProcess().getProcessId() == 2)
                    .map(ins -> Instruction.valueOfInstructionPdf(ins, null))
                    .collect(Collectors.toList());
            instructionResponsePdfDtos = null;
            inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry, instructionsCut, instructionsSlit);
        } else if (pdfDto.getProcessId() != null) {
            inwardEntry = inwardEntryService.getByEntryId(pdfDto.getInwardId());
            instructionResponsePdfDtos = inwardEntry.getInstructions()
                    .stream().filter(ins -> ins.getProcess().getProcessId() == pdfDto.getProcessId())
                    .map(i -> Instruction.valueOfInstructionPdf(i, null))
                    .collect(Collectors.toList());
            inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry, instructionResponsePdfDtos);
        } else {
            inwardEntry = inwardEntryService.getByEntryId(pdfDto.getInwardId());
            instructionResponsePdfDtos = null;
            inwardEntryPdfDto = InwardEntry.valueOf(inwardEntry, instructionResponsePdfDtos);
        }
        context.setVariable("inward", inwardEntryPdfDto);
        return context;
    }

    private Context getContext(PartDto partDto) {
        Context context = new Context();
        InwardEntryPdfDto inwardEntryPdfDto = instructionService.findQRCodeInwardJoinFetchInstructionsAndPartDetails(partDto.getPartDetailsId(), partDto.getGroupIds());
        context.setVariable("inward", inwardEntryPdfDto);
        return context;
    }

    private String loadAndFillTemplate(Context context, Integer processId) {
        if (processId != null && processId == 1) {
            return templateEngine.process("Cutting-slip", context);
        } else if (processId != null && processId == 2) {
            return templateEngine.process("Slitting-slip", context);
        } else if (processId != null && processId == 3) {
            return templateEngine.process("SlitAndCut-slip", context);
        } else {
            return templateEngine.process("Inward",context);
        }
    }

	public File renderFGLabelPrintPDF(Integer inwardId, InstructionFinishDto instructionFinishDto) throws IOException {
		File labelFile = File.createTempFile("labelprintFG_" + System.currentTimeMillis(), ".pdf");
		try {
			LabelPrintDTO labelPrintDTO = new LabelPrintDTO();
			labelPrintDTO.setInwardEntryId(inwardId);
			labelFile = labelPrintPDFGenerator.renderFGLabelPrintPDF(labelPrintDTO, instructionFinishDto, labelFile);
			awsS3Service.uploadPDFFileToS3Bucket(bucketName, labelFile, "FGLabel_" + inwardId);
			inwardEntryService.updateS3InwardLabelPDF(inwardId, "FGLabel_" + inwardId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labelFile;
	}

	public File labelPrint(InstructionFinishDto instructionFinishDto) throws Exception {

		File labelFile = File.createTempFile("labelprintfg_" + System.currentTimeMillis(), ".pdf");
		boolean generateLabel = false;

		try {
			Instruction instructions = instructionService.getById(instructionFinishDto.getInstructionDtos().get(0).getInstructionId());

			LabelPrintDTO labelPrintDTO = new LabelPrintDTO();
			labelPrintDTO.setInwardEntryId(instructions.getInwardId().getInwardEntryId());
			labelPrintDTO.setProcess("fg");

			if ("WIPtoFG".equalsIgnoreCase(instructionFinishDto.getTaskType())) { // WIPtoFG
				generateLabel = true;
			} else { // FGtoFG .... edit finish
				generateLabel = true;
			}
			if (generateLabel) {
				Integer lastReqId = null;
				try {
					lastReqId = fgLabelRepository.getLastReqId(labelPrintDTO.getInwardEntryId());
				} catch (Exception ex) {
				}

				if (lastReqId == null) {
					lastReqId = 1;
				} else {
					lastReqId = lastReqId + 1;
				}

				labelFile = labelPrintPDFGenerator.renderFGLabelPrintPDF(labelPrintDTO, instructionFinishDto, labelFile);
				awsS3Service.uploadPDFFileToS3Bucket(bucketName, labelFile, "FGLabel_" + labelPrintDTO.getInwardEntryId() + "_" + lastReqId);
				FGLabelEntity fgLabelEntity = new FGLabelEntity();
				fgLabelEntity.setInwardentryid(labelPrintDTO.getInwardEntryId());
				fgLabelEntity.setLabelName("FGLabel_" + labelPrintDTO.getInwardEntryId() + "_" + lastReqId);
				fgLabelEntity.setLabelS3Url("FGLabel_" + labelPrintDTO.getInwardEntryId() + "_" + lastReqId);
				fgLabelEntity.setLabelSeq(lastReqId);
				fgLabelRepository.save(fgLabelEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		labelFile.deleteOnExit();
		return labelFile;
	}
	
	 
}
