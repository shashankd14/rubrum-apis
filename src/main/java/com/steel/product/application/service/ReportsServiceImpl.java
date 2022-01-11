package com.steel.product.application.service;

import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.util.CSVUtil;
import com.steel.product.application.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    private final InwardEntryService inwardEntryService;
    private final CSVUtil csvUtil;
    private final EmailUtil emailUtil;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Value("#{'${stock.report.headers}'.split(',')}")
    private List<String> stockReportHeaders;

    @Autowired
    public ReportsServiceImpl(InwardEntryService inwardEntryService, CSVUtil csvUtil, EmailUtil emailUtil) {
        this.inwardEntryService = inwardEntryService;
        this.csvUtil = csvUtil;
        this.emailUtil = emailUtil;
    }

    @Override
    public String generateAndMailStockReport(StockReportRequest stockReportRequest) {
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
        if(report.exists()) {
            report.delete();
        }
        return "email sent ok !!";
    }

//    @Override
//    public void uploadCSV() throws IOException, ParseException {
//        FileReader in = new FileReader("RM-REPORT-CSV.csv");
////        CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT);
//        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
////        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
////        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//        for(CSVRecord rec:records){
//            InwardDto dto = new InwardDto();
//            dto.setCoilNumber(rec.get(0));
//            dto.setBatchNumber(rec.get(1));
//            dto.setCustomerBatchId(rec.get(2));
//            dto.setInwardDate(rec.get(3)+" 00:00:00");
//            String matDesc = rec.get(4);
//            Material mat = materialDescriptionService.findByDesc(matDesc);
//            dto.setThickness(Float.parseFloat(rec.get(5)));
//            dto.setWidth(Float.parseFloat(rec.get(6)));
//            dto.setPresentWeight(Float.parseFloat(rec.get(7)));
//            float denominator = dto.getThickness() * (dto.getWidth() / 1000f) * 7.85f;
//            float lengthUnprocessed = dto.getPresentWeight() / denominator;
//            dto.setLength(lengthUnprocessed);
//            dto.setGrossWeight(Float.parseFloat(rec.get(8)));
//            dto.setInvoiceNumber(rec.get(9));
//            dto.setInvoiceDate(rec.get(10)+" 00:00:00");
//            dto.setParentCoilNumber(rec.get(11));
//            String gradeName = rec.get(12);
//            MaterialGrade mg = materialGradeRepo.findByGradeName(gradeName);
//            dto.setTestCertificateNumber(rec.get(13));
//            dto.setValueOfGoods(Float.parseFloat(rec.get(14)));
//            dto.setPurposeType(rec.get(15));
//            dto.setRemarks(rec.get(16));
//            if(mat == null){
//                mat = new Material();
//                mat.setDescription(matDesc);
//                mat = materialDescriptionRepository.save(mat);
//            }
//            dto.setMaterialId(mat.getMatId());
//            if(mg == null){
//                mg = new MaterialGrade();
//                mg.setGradeName(gradeName);
//                mg.setParentMaterial(mat);
//                materialGradeRepo.save(mg);
//            }
//            dto.setMaterialGradeId(mg.getGradeId());
//            dto.setPartyId(8);
////            String d = "2022-01-06"+" 00:00:00";
////            dto.setInwardDate(d);
////            dto.setBillDate(d);
//            dto.setCreatedBy(1);
//            dto.setUpdatedBy(1);
//            this.saveInwardEntry(dto);
//
//
//        }
//    }
//
//    public void saveInwardEntry(@ModelAttribute InwardDto inward) throws ParseException {
//        InwardEntry inwardEntry = new InwardEntry();
//            inwardEntry.setInwardEntryId(0);
//            inwardEntry.setPurposeType(inward.getPurposeType());
//            inwardEntry.setParty(this.partyDetailsService.getPartyById(inward.getPartyId()));
//            inwardEntry.setCoilNumber(inward.getCoilNumber());
//            inwardEntry.setBatchNumber(inward.getBatchNumber());
//            inwardEntry.setdReceivedDate(sdf.parse(inward.getInwardDate()));
//            inwardEntry.setInStockWeight(inward.getPresentWeight());
//
//            if(inward.getBillDate()!=null)
//                inwardEntry.setdBillDate(Timestamp.valueOf(inward.getBillDate()));
//
//            inwardEntry.setvLorryNo(inward.getVehicleNumber());
//            inwardEntry.setvInvoiceNo(inward.getInvoiceNumber());
//            inwardEntry.setdInvoiceDate(sdf.parse(inward.getInvoiceDate()));
//
//            inwardEntry.setCustomerCoilId(String.valueOf(inward.getPartyId()));
//            inwardEntry.setCustomerInvoiceNo(inward.getInvoiceNumber());
//            inwardEntry.setCustomerBatchId(inward.getCustomerBatchId());
//
//
//            //inwardEntry.setCustomerInvoiceDate(Timestamp.valueOf(inward.getCustomerInvoiceDate()));
//
//            inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
//            inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));
//
//            inwardEntry.setfWidth(inward.getWidth());
//            inwardEntry.setfThickness(inward.getThickness());
//            inwardEntry.setfLength(inward.getLength());
//            inwardEntry.setAvailableLength(inward.getLength());
//            inwardEntry.setfQuantity(inward.getPresentWeight());
//            inwardEntry.setGrossWeight(inward.getGrossWeight());
//            inwardEntry.setTestCertificateNumber(inward.getTestCertificateNumber());
//
//            //inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
//            inwardEntry.setStatus(this.statusService.getStatusById(1));
//
//            inwardEntry.setvProcess(inward.getProcess());
//            inwardEntry.setFpresent(inward.getPresentWeight());
//            inwardEntry.setValueOfGoods(inward.getValueOfGoods());
//
//            inwardEntry.setBilledweight(0);
//            inwardEntry.setvParentBundleNumber(0);
//
//            inwardEntry.setRemarks(inward.getRemarks());
//            inwardEntry.setParentCoilNumber(inward.getParentCoilNumber());
//
//            inwardEntry.setIsDeleted(Boolean.valueOf(false));
//            inwardEntry.setCreatedOn(new Date());
//            inwardEntry.setUpdatedOn(new Date());
//
//            inwardEntry.setCreatedBy(this.userService.getUserById(inward.getCreatedBy()));
//            inwardEntry.setUpdatedBy(this.userService.getUserById(inward.getUpdatedBy()));
//            inwardEntryService.saveEntry(inwardEntry);
//
//
//    }


}
