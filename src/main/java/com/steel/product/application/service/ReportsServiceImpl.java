package com.steel.product.application.service;

import com.steel.product.application.controller.InwardEntryController;
import com.steel.product.application.dao.MaterialDescriptionRepository;
import com.steel.product.application.dao.MaterialGradeRepository;
import com.steel.product.application.dto.inward.InwardDto;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Material;
import com.steel.product.application.util.CSVUtil;
import com.steel.product.application.util.EmailUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    private final InwardEntryService inwardEntryService;
    private final CSVUtil csvUtil;
    private final EmailUtil emailUtil;
    private final MaterialDescriptionService materialDescriptionService;
    private final MaterialGradeRepository materialGradeRepo;
    private final InwardEntryController inwardEntryController;
    private final MaterialDescriptionRepository materialDescriptionRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Value("#{'${stock.report.headers}'.split(',')}")
    private List<String> stockReportHeaders;

    public ReportsServiceImpl(InwardEntryService inwardEntryService, CSVUtil csvUtil, EmailUtil emailUtil, MaterialDescriptionService materialDescriptionService, MaterialGradeRepository materialGradeRepo, InwardEntryController inwardEntryController, MaterialDescriptionRepository materialDescriptionRepository) {
        this.inwardEntryService = inwardEntryService;
        this.csvUtil = csvUtil;
        this.emailUtil = emailUtil;
        this.materialDescriptionService = materialDescriptionService;
        this.materialGradeRepo = materialGradeRepo;
        this.inwardEntryController = inwardEntryController;
        this.materialDescriptionRepository = materialDescriptionRepository;
    }

    @Override
    public void generateAndMailStockReport(StockReportRequest stockReportRequest) {
        LOGGER.info("inside generateAndMailStockReport method");
        String startDate = stockReportRequest.getFromDate()+" 00:00:00";
        String endDate = stockReportRequest.getToDate()+" 23:59:59";

        Date d1 = null,d2 = null;
        try {
            d1 = sdf.parse(startDate);
            d2 = sdf.parse(endDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        List<InwardEntry> inwardEntries = inwardEntryService.findInwardByPartyIdAndCreatedOnBetween(stockReportRequest.getPartyId(),d1,d2);
        if(inwardEntries.isEmpty()){
            throw new RuntimeException("No inwards found between "+startDate+" and "+endDate+" for party id "+stockReportRequest.getPartyId());
        }
        String email = inwardEntries.get(0).getParty().getEmail1();
        String[] headers = stockReportHeaders.toArray(new String[0]);
        File report = csvUtil.generateStockReportCSV(headers,inwardEntries);
        emailUtil.sendEmail(report,email);
        report.delete();
        LOGGER.debug("file deleted ok ");
    }

    @Override
    public void uploadCSV() throws IOException, ParseException {
        FileReader in = new FileReader("RM_REPORT_CSV.csv");
//        CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        for(CSVRecord rec:records){
            InwardDto dto = new InwardDto();
            dto.setCoilNumber(rec.get(0));
            dto.setBatchNumber(rec.get(1));
            String matDesc = rec.get(2);
            Material mat = materialDescriptionService.findByDesc(matDesc);
            dto.setThickness(Float.parseFloat(rec.get(3)));
            dto.setWidth(Float.parseFloat(rec.get(4)));
            dto.setPresentWeight(Float.parseFloat(rec.get(5)));
            dto.setGrossWeight(Float.parseFloat(rec.get(6)));
            dto.setInvoiceNumber(rec.get(7));
            dto.setInvoiceDate(rec.get(8));
//            dto.setParentCoilNumber(rec.get(9));
            String gradeName = rec.get(10);
//            MaterialGrade mg = materialGradeRepo.findByGradeName(gradeName);
            dto.setTestCertificateNumber(rec.get(11));
            dto.setValueOfGoods(Float.parseFloat(rec.get(12)));
            dto.setPurposeType(rec.get(22));
            dto.setRemarks(rec.get(23));
            if(mat == null){
                mat = new Material();
                mat.setDescription(matDesc);
                mat = materialDescriptionRepository.save(mat);
            }
            dto.setMaterialId(mat.getMatId());
//            if(mg == null){
//                mg = new MaterialGrade();
//                mg.setGradeName(gradeName);
//                mg.setParentMaterial(mat);
//                materialGradeRepo.save(mg);
//            }
//            dto.setMaterialGradeId(mg.getGradeId());
            dto.setPartyId(1);
            String d = "2022-01-06"+" 00:00:00";
            dto.setInwardDate(d);
            dto.setBillDate(d);
            dto.setCreatedBy(1);
            dto.setUpdatedBy(1);
            inwardEntryController.saveInwardEntry(dto);


        }
    }


}
