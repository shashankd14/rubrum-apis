package com.steel.product.application.service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.FGReportViewRepository;
import com.steel.product.application.dao.InwardReportViewRepository;
import com.steel.product.application.dao.MonthlySummaryReportRepository;
import com.steel.product.application.dao.MonthwisePlanTrackerEndUserwiseViewRepository;
import com.steel.product.application.dao.MonthwisePlanTrackerViewRepository;
import com.steel.product.application.dao.OutwardReportViewRepository;
import com.steel.product.application.dao.ProcessingReportViewRepository;
import com.steel.product.application.dao.RMReportViewRepository;
import com.steel.product.application.dao.StockDetailsReportViewRepository;
import com.steel.product.application.dao.StockReportViewRepository;
import com.steel.product.application.dao.StockSummaryReportViewRepository;
import com.steel.product.application.dao.WIPReportViewRepository;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.FGReportViewEntity;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.InwardReportViewEntity;
import com.steel.product.application.entity.MonthlySummaryReportEntity;
import com.steel.product.application.entity.MonthwisePlanTrackerEnduserViewEntity;
import com.steel.product.application.entity.MonthwisePlanTrackerViewEntity;
import com.steel.product.application.entity.OutwardReportViewEntity;
import com.steel.product.application.entity.ProcessingReportViewEntity;
import com.steel.product.application.entity.RMReportViewEntity;
import com.steel.product.application.entity.StockDetailsReportViewEntity;
import com.steel.product.application.entity.StockReportViewEntity;
import com.steel.product.application.entity.StockSummaryReportViewEntity;
import com.steel.product.application.entity.WIPReportViewEntity;
import com.steel.product.application.util.CSVUtil;
import com.steel.product.application.util.EmailUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ReportsServiceImpl implements ReportsService {

    private final InwardEntryService inwardEntryService;
    private final InstructionService instructionService;
    private final CSVUtil            csvUtil;
    private final EmailUtil          emailUtil;

    @Autowired OutwardReportViewRepository                   outwardReportViewRepository;
    @Autowired InwardReportViewRepository                    inwardReportViewRepository;
    @Autowired MonthlySummaryReportRepository                monthlySummaryReportRepository;
    @Autowired StockReportViewRepository                     stockReportViewRepository;
    @Autowired StockDetailsReportViewRepository              stockDetailsReportViewRepository;
    @Autowired FGReportViewRepository                        fgReportViewRepository;
    @Autowired MonthwisePlanTrackerViewRepository            monthwisePlanTrackerViewRepository;
    @Autowired MonthwisePlanTrackerEndUserwiseViewRepository endUserwiseViewRepository;
    @Autowired WIPReportViewRepository                       wipReportViewRepository;
    @Autowired RMReportViewRepository                        rmReportViewRepository;
    @Autowired StockSummaryReportViewRepository              stockSummaryReportViewRepository;
    @Autowired ProcessingReportViewRepository                processingRepository;
    @Autowired Environment                                   env;

    @Value("#{'${stock.report.headers}'.split(',')}")
    private List<String> stockReportHeaders;

    @Autowired
    public ReportsServiceImpl(InwardEntryService inwardEntryService,
                              InstructionService instructionService,
                              CSVUtil csvUtil,
                              EmailUtil emailUtil) {
        this.inwardEntryService = inwardEntryService;
        this.instructionService = instructionService;
        this.csvUtil            = csvUtil;
        this.emailUtil          = emailUtil;
    }

    // =========================================================================
    // SHARED EXCEL UTILITIES
    // =========================================================================

    /**
     * All cell styles for one workbook.
     * Created ONCE per workbook — never inside a row/cell loop.
     * POI's 64k style limit is safe: we create exactly 5 styles total.
     */
    private static class WorkbookStyles {
        final CellStyle text;
        final CellStyle integer;
        final CellStyle decimal;   // 2 dp
        final CellStyle decimal3;  // 3 dp
        final CellStyle header;    // bold + yellow, row 0 only

        WorkbookStyles(XSSFWorkbook wb) {
            DataFormat fmt = wb.createDataFormat();

            text = wb.createCellStyle();
            text.setBorderTop   (BorderStyle.THIN);
            text.setBorderBottom(BorderStyle.THIN);
            text.setBorderLeft  (BorderStyle.THIN);
            text.setBorderRight (BorderStyle.THIN);
            text.setAlignment   (HorizontalAlignment.CENTER);

            integer = wb.createCellStyle();
            integer.cloneStyleFrom(text);
            integer.setAlignment (HorizontalAlignment.RIGHT);
            integer.setDataFormat(fmt.getFormat("###0"));

            decimal = wb.createCellStyle();
            decimal.cloneStyleFrom(text);
            decimal.setAlignment (HorizontalAlignment.RIGHT);
            decimal.setDataFormat(fmt.getFormat("###0.00"));

            decimal3 = wb.createCellStyle();
            decimal3.cloneStyleFrom(text);
            decimal3.setAlignment (HorizontalAlignment.RIGHT);
            decimal3.setDataFormat(fmt.getFormat("###0.000"));

            XSSFFont boldFont = wb.createFont();
            boldFont.setBold (true);
            boldFont.setColor(IndexedColors.BLACK.getIndex());

            XSSFCellStyle hdr = wb.createCellStyle();
            hdr.cloneStyleFrom(text);
            hdr.setFont               (boldFont);
            hdr.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            hdr.setFillPattern        (FillPatternType.SOLID_FOREGROUND);
            hdr.setAlignment          (HorizontalAlignment.CENTER);
            header = hdr;
        }
    }

    /**
     * Writes one data cell with the correct numeric/text style.
     * Picks decimal3 vs decimal by inspecting significant decimal digits.
     */
    private void populateCell(Cell cell, Object value, WorkbookStyles styles) {
        if (value == null) {
            cell.setCellStyle(styles.text);
            cell.setCellValue("");
            return;
        }
        if (value instanceof Integer || value instanceof Long) {
            cell.setCellStyle(styles.integer);
            cell.setCellValue(((Number) value).longValue());
            return;
        }
        if (value instanceof BigDecimal) {
            BigDecimal bd   = (BigDecimal) value;
            boolean    use3 = bd.scale() >= 3
                && bd.remainder(new BigDecimal("0.001")).compareTo(BigDecimal.ZERO) != 0;
            cell.setCellStyle(use3 ? styles.decimal3 : styles.decimal);
            cell.setCellValue(bd.doubleValue());
            return;
        }
        if (value instanceof Double || value instanceof Float) {
            double d        = ((Number) value).doubleValue();
            String stripped = Double.toString(d).replaceAll("0+$", "");
            int    dot      = stripped.indexOf('.');
            int    sigDec   = dot < 0 ? 0 : stripped.length() - dot - 1;
            cell.setCellStyle(sigDec >= 3 ? styles.decimal3 : styles.decimal);
            cell.setCellValue(d);
            return;
        }
        String strVal = value.toString().trim();
        if (!strVal.isEmpty()) {
            try {
                if (strVal.contains(".")) {
                    String afterDot = strVal.replaceAll("0+$", "").replaceFirst(".*\\.", "");
                    cell.setCellStyle(afterDot.length() >= 3 ? styles.decimal3 : styles.decimal);
                    cell.setCellValue(Double.parseDouble(strVal));
                } else {
                    cell.setCellStyle(styles.integer);
                    cell.setCellValue(Long.parseLong(strVal));
                }
                return;
            } catch (NumberFormatException ignored) { /* fall through to text */ }
        }
        cell.setCellStyle(styles.text);
        cell.setCellValue(strVal);
    }

    /**
     * Adds one sheet to the workbook.
     * Streams rows directly from the entity list — no intermediate Map buffer.
     * Uses setColumnWidth() — never autoSizeColumn() which loads AWT font metrics.
     * Header row is bold + yellow and frozen.
     */
    private <T> void addSheet(XSSFWorkbook wb,
                               String       sheetName,
                               Object[]     headers,
                               List<T>      rows,
                               Function<T, Object[]> rowMapper,
                               WorkbookStyles         styles) {

        Sheet  sheet    = wb.createSheet(sheetName);
        int    colCount = headers.length;
        int[]  maxLen   = new int[colCount];

        // Header row
        Row hRow = sheet.createRow(0);
        for (int c = 0; c < colCount; c++) {
            Cell   cell = hRow.createCell(c);
            String text = headers[c] != null ? headers[c].toString() : "";
            cell.setCellStyle(styles.header);
            cell.setCellValue(text);
            maxLen[c] = Math.min(text.length(), 60);
        }

        // Data rows — streamed directly from list, no Map
        int rowIdx = 1;
        for (T item : rows) {
            Object[] cols = rowMapper.apply(item);
            Row      row  = sheet.createRow(rowIdx++);
            for (int c = 0; c < cols.length && c < colCount; c++) {
                Cell   cell = row.createCell(c);
                String repr = cols[c] != null ? cols[c].toString() : "";
                populateCell(cell, cols[c], styles);
                if (repr.length() > maxLen[c]) maxLen[c] = Math.min(repr.length(), 60);
            }
        }

        // Fixed column widths — no AWT, no autoSizeColumn
        // POI unit = 1/256th char width; ~310 ≈ 1 char in Arial 10pt
        for (int i = 0; i < colCount; i++) {
            sheet.setColumnWidth(i, Math.max(maxLen[i], 12) * 310);
        }

        // Freeze header row
        sheet.createFreezePane(0, 1);
    }

    /**
     * Writes workbook to disk, attaches to email when hasData=true,
     * schedules file deletion on JVM exit.
     * Workbook is closed by the try-with-resources in the caller.
     *
     * @return true  = no data, no attachment
     *         false = attachment added
     */
    private boolean writeAndAttach(XSSFWorkbook      wb,
                                   String            fileName,
                                   boolean           hasData,
                                   MimeMessageHelper helper) throws Exception {
        String baseDir = env.getProperty("email.folderpath") + File.separator;
        new File(baseDir).mkdirs();
        File fullPath = new File(baseDir, fileName);

        try (FileOutputStream out = new FileOutputStream(fullPath)) {
            wb.write(out);
        }

        if (hasData) {
            helper.addAttachment(fileName, new FileSystemResource(fullPath));
        }
        fullPath.deleteOnExit();
        return !hasData;
    }

    // ── Shared header arrays (declared once, reused across methods) ───────────

    private static final Object[] FG_HEADERS = {
        "CoilNumber","CustomerBatchId","Finishing Date","Current Date","Coil Age(No'of Days)",
        "No'of Pieces","MaterialDesc","MaterialGrade","Mother Coil No","TDC No","Remarks",
        "Packet Id","Thickness","Actual Width","Actual Length","Actual Weight",
        "Classification Tag","End User Tag"
    };

    private static final Object[] WIP_HEADERS = {
        "CoilNumber","CustomerBatchId","Processing Plan Date","Current Date",
        "Coil Age(No'of Days)","MaterialDesc","MaterialGrade","Mother Coil No","TDC No",
        "Thickness","Width","Length","Net Weight","In Stock Weight","WIP Weight",
        "Remarks","Packet id","Thickness","Planned Width","Planned Length",
        "Planned Weight","Inward Status","Classification Tag","End User Tag"
    };

    private static final Object[] PROCESSING_HEADERS = {
        "CoilNumber","CustomerBatchId","CustomerName","ProcessName","MaterialDesc",
        "MaterialGrade","ProcessDate","PacketId","Packet Thickness","Packet Width",
        "Packet Length","Finishing Weight","Finishing Date","End User Tag"
    };

    // ── Shared row mappers (static — no heap allocation per call) ─────────────

    private static Object[] fgRow(FGReportViewEntity kk) {
        return new Object[]{
            kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getFinishingDate(),
            kk.getCurrentdate(), kk.getCoilage(), kk.getNoofpieces(), kk.getMaterialDesc(),
            kk.getMaterialGrade(), kk.getBatchnumber(), kk.getTdcNo(), kk.getRemarks(),
            kk.getPacketId(), kk.getThickness(), kk.getActualwidth(), kk.getActuallength(),
            kk.getActualweight(), kk.getClassificationTag(),
            (kk.getEnduserTagName() != null && !kk.getEnduserTagName().isEmpty()
                ? kk.getEnduserTagName() : "")
        };
    }

    private static Object[] wipRow(WIPReportViewEntity kk) {
        return new Object[]{
            kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getProcessingPlanDate(),
            kk.getCurrentdate(), kk.getCoilage(), kk.getMaterialDesc(), kk.getMaterialGrade(),
            kk.getBatchnumber(), kk.getTdcNo(), kk.getFthickness(), kk.getFwidth(),
            kk.getFlength(), kk.getNetWeight(), kk.getInStockWeight(), kk.getWipWeight(),
            kk.getRemarks(), kk.getPacketId(), kk.getThickness(), kk.getPlannedWidth(),
            kk.getPlannedLength(), kk.getPlannedWeight(), kk.getInwardStatus(),
            kk.getClassificationTag(), kk.getEnduserTagName()
        };
    }

    private static Object[] processingRow(ProcessingReportViewEntity kk) {
        return new Object[]{
            kk.getCoilnumber(), kk.getCustomerbatchid(), kk.getCustomerName(),
            kk.getProcessName(), kk.getMaterialdesc(), kk.getMaterialGrade(),
            kk.getProcessdate(), kk.getPacketId(), kk.getPacketThickness(),
            kk.getPacketWidth()     == null ? "" : kk.getPacketWidth(),
            kk.getPacketLength()    == null ? "" : kk.getPacketLength(),
            kk.getFinishingWeight() == null ? "" : kk.getFinishingWeight(),
            kk.getFinishingWeight() == null ? "" : kk.getFinishingDate(),
            kk.getEnduser_tag_name()== null ? "" : kk.getEnduser_tag_name()
        };
    }

    // =========================================================================
    // REPORT METHODS
    // =========================================================================

    @Override
    public String generateAndMailStockReport(StockReportRequest stockReportRequest) {
        log.info("inside generateAndMailStockReport");
        List<InwardEntry> inwardEntries =
            inwardEntryService.findInwardByPartyId(stockReportRequest.getPartyId());
        if (inwardEntries.isEmpty())
            throw new RuntimeException("No inwards found for party id " + stockReportRequest.getPartyId());

        HashMap<Integer, Double> unprocessedWeights =
            instructionService.findSumOfPlannedWeightAndActualWeightForUnprocessed();
        log.info("unprocessed weights {}", unprocessedWeights.size());

        String   email   = inwardEntries.get(0).getParty().getEmail1();
        String[] headers = stockReportHeaders.toArray(new String[0]);
        File     report  = csvUtil.generateStockReportCSV(headers, inwardEntries, unprocessedWeights);
        if (report == null) return "error in generating csv";
        emailUtil.sendEmail(report, email);
        if (report.exists()) report.delete();
        return "email sent ok !!";
    }

    // ── Stock Report ──────────────────────────────────────────────────────────

    @Override
    public boolean createStockReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles styles = new WorkbookStyles(wb);
            List<StockReportViewEntity> list = stockReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "Stock_Report",
                new Object[]{"CoilNumber","CustomerBatchId","Mother Coil No","TDC No","Coil Age",
                    "MaterialDesc","MaterialGrade","Thickness","Width","Length",
                    "NetWeight","UnprocessedWeight","InStockWeight","Remarks","InwardStatus"},
                list,
                kk -> new Object[]{
                    kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getBatchnumber(), kk.getTdcNo(),
                    kk.getCoilage(), kk.getMaterialDesc(), kk.getMaterialGrade(), kk.getFthickness(),
                    kk.getFwidth(), kk.getFlength(), kk.getNetWeight(), kk.getUnProcessedWeight(),
                    kk.getInStockWeight(), kk.getRemarks(), kk.getInwardStatus()},
                styles);
            return writeAndAttach(wb, "StockReport_" + strDate + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createStockReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── FG Report (3 sheets) ──────────────────────────────────────────────────

    @Override
    public boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles           styles = new WorkbookStyles(wb);
            List<FGReportViewEntity> all    = fgReportViewRepository.findByPartyId(partyId);

            Set<String> otherTags = new HashSet<>(Arrays.asList(
                "EDGE TRIM","CUT ENDS","WIP(NCO)","WIP(FG)","WIP(END CUT)",
                "WIP(EDGE TRIM)","WIP(CUT ENDS)","WIP (SFCP)","NCO"));
            Set<String> excludeDefects = new HashSet<>(otherTags);
            excludeDefects.add("FG");

            List<FGReportViewEntity> fgList      = new ArrayList<>();
            List<FGReportViewEntity> othersList  = new ArrayList<>();
            List<FGReportViewEntity> defectsList = new ArrayList<>();

            for (FGReportViewEntity kk : all) {
                String tag = kk.getClassificationTag();
                if ("FG".equals(tag))
                    fgList.add(kk);
                if (tag != null && otherTags.contains(tag.trim().toUpperCase()))
                    othersList.add(kk);
                if (tag != null && !excludeDefects.contains(tag.trim().toUpperCase()))
                    defectsList.add(kk);
            }

            addSheet(wb, "FG_Classification",    FG_HEADERS, fgList,      ReportsServiceImpl::fgRow, styles);
            addSheet(wb, "Others_Classification", FG_HEADERS, othersList,  ReportsServiceImpl::fgRow, styles);
            addSheet(wb, "Quality_Defects",       FG_HEADERS, defectsList, ReportsServiceImpl::fgRow, styles);

            return writeAndAttach(wb, "FGReport_" + strDate + ".xlsx",
                !fgList.isEmpty() || !othersList.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createFGReport failed partyId={}", partyId, e);
        }
        return false;
    }

    // ── WIP Report ────────────────────────────────────────────────────────────

    @Override
    public boolean createWIPReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles            styles = new WorkbookStyles(wb);
            List<WIPReportViewEntity> list   = wipReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "WIP_Report", WIP_HEADERS, list, ReportsServiceImpl::wipRow, styles);
            return writeAndAttach(wb, "WIPReport_" + strDate + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createWIPReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── WIP End-User-Tag-wise (multi-sheet) ───────────────────────────────────

    @Override
    public boolean createWIPReportEndusertagwise(Integer partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles            styles = new WorkbookStyles(wb);
            List<WIPReportViewEntity> all    = wipReportViewRepository.findByPartyId(partyId);

            // Group by end-user tag in one pass — no duplicate DB call
            Map<String, List<WIPReportViewEntity>> byTag = new LinkedHashMap<>();
            for (WIPReportViewEntity kk : all) {
                String tag = (kk.getEnduserTagName() != null && !kk.getEnduserTagName().isEmpty())
                    ? kk.getEnduserTagName() : "NO_ENDUSERTAG";
                byTag.computeIfAbsent(tag, k -> new ArrayList<>()).add(kk);
            }
            for (Map.Entry<String, List<WIPReportViewEntity>> e : byTag.entrySet()) {
                addSheet(wb, e.getKey() + "_EndUserTag", WIP_HEADERS,
                    e.getValue(), ReportsServiceImpl::wipRow, styles);
            }
            return writeAndAttach(wb, "WIP_EndUserTagwise_" + strDate + ".xlsx", !all.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createWIPReportEndusertagwise failed partyId={}", partyId, e);
        }
        return false;
    }

    // ── Stock Summary Report ──────────────────────────────────────────────────

    @Override
    public boolean createStockSummaryReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles                    styles = new WorkbookStyles(wb);
            List<StockSummaryReportViewEntity> list  = stockSummaryReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "StockSummary_Report",
                new Object[]{"Coil No","Batch No","MaterialDesc","MaterialGrade","Mother Coil No",
                    "TDC No","Thickness","Width","Length","NetWeight","InStockWeight","FG Qty",
                    "FG_Classification","CUT-ENDS_Classification","EDGE-TRIM_Classification",
                    "OTHERS_Classification","WIP_Classification","BLANK_Classification",
                    "Quality Defects","UnprocessedWeight","WIP Qty","Dispatched Qty","Remarks","InwardStatus"},
                list,
                kk -> new Object[]{
                    kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getMaterialDesc(),
                    kk.getMaterialGrade(), kk.getBatchnumber(), kk.getTdcNo(), kk.getFthickness(),
                    kk.getFwidth(), kk.getFlength(), kk.getNetweight(), kk.getInstockweight(),
                    kk.getFgqty(), kk.getFgclassification(), kk.getCutendsclassification(),
                    kk.getEdgetrimclassification(), kk.getOthersclassification(),
                    kk.getWipclassification(), kk.getBlankclassification(), kk.getQualitydefects(),
                    kk.getUnprocessedweight(), kk.getWipqty(), kk.getDispatchedweight(),
                    kk.getRemarks(), kk.getInwardstatus()},
                styles);
            return writeAndAttach(wb, "StockSummaryReport_" + strDate + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createStockSummaryReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── RM Report ─────────────────────────────────────────────────────────────

    @Override
    public boolean createRMReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles           styles = new WorkbookStyles(wb);
            List<RMReportViewEntity> list   = rmReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "RM_Report",
                new Object[]{"CoilNumber","CustomerBatchId","Mother Coil No","Received Date",
                    "Current Date","Coil Age(No'of Days)","MaterialDesc","MaterialGrade","TDC No",
                    "Thickness","Width","Length","Net Weight","Customer Invoice Number",
                    "Customer Invoice Date","Status","Created On","Remarks"},
                list,
                kk -> new Object[]{
                    kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getParentcoilnumber(),
                    kk.getReceivedDate(), kk.getCurrentdate(), kk.getCoilage(),
                    kk.getDescription(), kk.getMaterialGrade(), kk.getTdcNo(), kk.getFthickness(),
                    kk.getFwidth(), kk.getFlength(), kk.getNetWeight(), kk.getCustInvNo(),
                    kk.getCustInvDate(), kk.getInwardStatus(), kk.getCreatedOn(), kk.getRemarks()},
                styles);
            return writeAndAttach(wb, "RMReport_" + strDate + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createRMReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Reconcile ─────────────────────────────────────────────────────────────

    @Override
    public List<StockSummaryReportViewEntity> reconcileReport(String coilNumber) {
        return stockSummaryReportViewRepository.findByCoilNumber(coilNumber);
    }

    // ── Inward Monthly Report ─────────────────────────────────────────────────

    @Override
    public boolean createInwardMonthlyReport(Integer partyId, MimeMessageHelper helper,
                                             Integer month, Map<Integer, String> months, Integer year) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles               styles = new WorkbookStyles(wb);
            List<InwardReportViewEntity> list   =
                inwardReportViewRepository.findByPartyIdAndMnthAndYer(partyId, month, year);
            addSheet(wb, "Inward_Report",
                new Object[]{"CustomerName","CoilNumber","CustomerBatchId","ReceivedDate","MaterialDesc",
                    "MaterialGrade","TDC No","Thickness","Width","Length","NetWeight",
                    "customerinvoiceno","customerinvoicedate","InwardStatus"},
                list,
                kk -> new Object[]{
                    kk.getCustomerName(), kk.getCoilnumber(), kk.getCustomerbatchid(),
                    kk.getReceivedDate(), kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getTdcNo(),
                    kk.getFthickness(), kk.getFwidth(), kk.getFlength(), kk.getNetWeight(),
                    kk.getCustomerinvoiceno(), kk.getCustomerinvoicedate(), kk.getInwardStatus()},
                styles);
            return writeAndAttach(wb, "InwardReport_" + months.get(month) + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createInwardMonthlyReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Outward Monthly Report ────────────────────────────────────────────────

    @Override
    public boolean createOutwardMonthlyReport(Integer partyId, MimeMessageHelper helper,
                                              Integer month, Map<Integer, String> months, Integer year) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles                styles = new WorkbookStyles(wb);
            List<OutwardReportViewEntity> list   =
                outwardReportViewRepository.findByPartyIdAndMnthAndYer(partyId, month, year);

            Object[] hdrs = {"S.No","Aspen DC No","customerbatchid","material description","vehicleno",
                "customer name","sap invoice No","sap invoice date","material grade","tdc no",
                "process type","Qty","Base Rate","Packing Charges","Lamination Charges",
                "Additional Charges","Rate","Total Amount"};

            Sheet  sheet  = wb.createSheet("Outward_Report");
            int[]  maxLen = new int[hdrs.length];

            Row hRow = sheet.createRow(0);
            for (int c = 0; c < hdrs.length; c++) {
                Cell cell = hRow.createCell(c);
                cell.setCellStyle(styles.header);
                cell.setCellValue(hdrs[c].toString());
                maxLen[c] = Math.min(hdrs[c].toString().length(), 60);
            }

            int        rowIdx      = 1;
            int        cnt         = 1;
            int        oldDCNo     = 0;
            BigDecimal totalweight = BigDecimal.ZERO;

            for (OutwardReportViewEntity kk : list) {
                if (oldDCNo > 0 && oldDCNo != kk.getAspendcno()) {
                    Object[] sub = {cnt,"","","","","","","","","","",
                        totalweight.divide(new BigDecimal("1000")).setScale(3, RoundingMode.HALF_UP),
                        "","","","","",""};
                    Row sr = sheet.createRow(rowIdx++);
                    for (int c = 0; c < sub.length; c++) {
                        populateCell(sr.createCell(c), sub[c], styles);
                    }
                }
                Object[] cols = {cnt, kk.getAspendcno(), kk.getCustomerbatchid(), kk.getMaterialgrade(),
                    kk.getVehicleno(), kk.getCustomername(), kk.getSapinvoiceno(), kk.getSapinvoicedate(),
                    kk.getMaterialdesc(), kk.getTdcNo(), kk.getProcessname(),
                    kk.getQty().divide(new BigDecimal("1000")).setScale(3, RoundingMode.HALF_UP),
                    kk.getBasePrice(), kk.getPackingCharges(), kk.getLaminationCharges(),
                    kk.getAdditionalCharges(), kk.getRate(),
                    kk.getTotalprice().setScale(2, RoundingMode.HALF_UP)};
                Row dr = sheet.createRow(rowIdx++);
                for (int c = 0; c < cols.length; c++) {
                    Cell cell = dr.createCell(c);
                    populateCell(cell, cols[c], styles);
                    String repr = cols[c] != null ? cols[c].toString() : "";
                    if (repr.length() > maxLen[c]) maxLen[c] = Math.min(repr.length(), 60);
                }
                oldDCNo     = kk.getAspendcno();
                totalweight = kk.getTotalweight();
                cnt++;
            }
            for (int i = 0; i < hdrs.length; i++) sheet.setColumnWidth(i, Math.max(maxLen[i], 12) * 310);
            sheet.createFreezePane(0, 1);

            return writeAndAttach(wb, "OutwardReport_" + months.get(month) + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createOutwardMonthlyReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Stock Monthly Report ──────────────────────────────────────────────────

    @Override
    public boolean createStockMonthlyReport(Integer partyId, MimeMessageHelper helper,
                                            Integer month, Map<Integer, String> months) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles              styles = new WorkbookStyles(wb);
            List<StockReportViewEntity> list   = stockReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "Stock_Report",
                new Object[]{"CoilNumber","CustomerBatchId","MaterialDesc","MaterialGrade","Thickness",
                    "Width","Length","NetWeight","UnprocessedWeight","InStockWeight","InwardStatus"},
                list,
                kk -> new Object[]{
                    kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getMaterialDesc(),
                    kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(), kk.getFlength(),
                    kk.getNetWeight(), kk.getUnProcessedWeight(), kk.getInStockWeight(),
                    kk.getInwardStatus()},
                styles);
            return writeAndAttach(wb, "StockReport_" + months.get(month) + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createStockMonthlyReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Processing Monthly Report ─────────────────────────────────────────────

    @Override
    public boolean createProcessingMonthlyReport(Integer partyId, MimeMessageHelper helper,
                                                 Integer month, Map<Integer, String> months, Integer year) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles                   styles = new WorkbookStyles(wb);
            List<ProcessingReportViewEntity> list   =
                processingRepository.findByPartyIdAndProcessmonthAndProcessyear(partyId, month, year);
            addSheet(wb, "Processing_Report", PROCESSING_HEADERS, list,
                ReportsServiceImpl::processingRow, styles);
            return writeAndAttach(wb, "ProcessingReport_" + months.get(month) + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createProcessingMonthlyReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Finishing Monthly Report ──────────────────────────────────────────────

    @Override
    public boolean createFinishingMonthlyReport(Integer partyId, MimeMessageHelper helper,
                                                Integer month, Map<Integer, String> months, Integer year) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles                   styles = new WorkbookStyles(wb);
            List<ProcessingReportViewEntity> list   =
                processingRepository.findByPartyIdAndFinishmonthAndFinishyear(partyId, month, year);
            addSheet(wb, "Finishing_Report", PROCESSING_HEADERS, list,
                ReportsServiceImpl::processingRow, styles);
            return writeAndAttach(wb, "FinishingReport_" + months.get(month) + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createFinishingMonthlyReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── End-User-Tag-wise FG Report (multi-sheet) ─────────────────────────────

    @Override
    public boolean createEndUserTagWiseFGReport(Integer partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles           styles = new WorkbookStyles(wb);
            List<FGReportViewEntity> all    = fgReportViewRepository.findByPartyId(partyId);

            Set<String> otherTags = new HashSet<>(Arrays.asList(
                "EDGE TRIM","CUT ENDS","WIP(NCO)","WIP(FG)","WIP(END CUT)",
                "WIP(EDGE TRIM)","WIP(CUT ENDS)","WIP (SFCP)","NCO"));

            // Group FG rows by end-user tag in one pass
            Map<String, List<FGReportViewEntity>> byTag    = new LinkedHashMap<>();
            List<FGReportViewEntity>              otherList = new ArrayList<>();

            for (FGReportViewEntity kk : all) {
                String tag = kk.getClassificationTag();
                if ("FG".equals(tag)) {
                    String utag = (kk.getEnduserTagName() != null && !kk.getEnduserTagName().isEmpty())
                        ? kk.getEnduserTagName() : "NO_ENDUSERTAG";
                    byTag.computeIfAbsent(utag, k -> new ArrayList<>()).add(kk);
                }
                if (tag != null && otherTags.contains(tag.trim().toUpperCase())) {
                    otherList.add(kk);
                }
            }

            for (Map.Entry<String, List<FGReportViewEntity>> e : byTag.entrySet()) {
                addSheet(wb, e.getKey() + "_EndUserTag", FG_HEADERS,
                    e.getValue(), ReportsServiceImpl::fgRow, styles);
            }
            addSheet(wb, "Others_Classification", FG_HEADERS, otherList,
                ReportsServiceImpl::fgRow, styles);

            return writeAndAttach(wb, "FG_EndUserTagWise_" + strDate + ".xlsx", !all.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createEndUserTagWiseFGReport failed partyId={}", partyId, e);
        }
        return false;
    }

    // ── Monthwise Plan Tracker (3 sheets) ─────────────────────────────────────

    private static final Map<Integer, String> MONTH_NAMES = new HashMap<>();
    static {
        MONTH_NAMES.put(1,"Jan");  MONTH_NAMES.put(2,"Feb");  MONTH_NAMES.put(3,"Mar");
        MONTH_NAMES.put(4,"Apr");  MONTH_NAMES.put(5,"May");  MONTH_NAMES.put(6,"Jun");
        MONTH_NAMES.put(7,"Jul");  MONTH_NAMES.put(8,"Aug");  MONTH_NAMES.put(9,"Sep");
        MONTH_NAMES.put(10,"Oct"); MONTH_NAMES.put(11,"Nov"); MONTH_NAMES.put(12,"Dec");
    }

    @Override
    public boolean createMonthwisePlanTrackerReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles styles = new WorkbookStyles(wb);

            LocalDate now           = LocalDate.now();
            int       currentMonth  = now.getMonthValue();
            int       previousMonth = currentMonth - 1;
            int       currentYear   = now.getYear();

            Object[] planHeaders = {
                "Plan pdf No","Plan Date","Finishing Date","Mother Coil No","Plan pdf qty",
                "Batch No","Aspen Coil No","Material Type","Material Grade","TDC No",
                "Inward Coil Weight","Packet Id","Thickness","Width","Length",
                "Quality Status","Packet Qty","Packet Status","End User"};

            Object[] euHeaders = {
                "Batch No","Material Type","Material Grade","Packet Id","Thickness",
                "Width","Length","Packet Qty","Packet Status","End User"};

            List<MonthwisePlanTrackerViewEntity> curList =
                monthwisePlanTrackerViewRepository.findByPartyidAndMnthAndYer(partyId, currentMonth, currentYear);
            List<MonthwisePlanTrackerViewEntity> prevList =
                now.getDayOfMonth() <= 10
                    ? monthwisePlanTrackerViewRepository.findByPartyidAndMnthAndYer(partyId, previousMonth, currentYear)
                    : new ArrayList<>();
            List<MonthwisePlanTrackerEnduserViewEntity> euList =
                endUserwiseViewRepository.findByPartyidAndMnthAndYer(partyId, previousMonth, currentYear);

            Function<MonthwisePlanTrackerViewEntity, Object[]> planMapper = kk -> new Object[]{
                kk.getPartdetailsid(), kk.getPlandate(), kk.getFinishingdate(),
                kk.getMothercoilno(), kk.getPlanPdfQty(), kk.getCustomerbatchid(),
                kk.getAspencoilno(), kk.getMaterialdesc(), kk.getMaterialgrade(), kk.getTdcNo(),
                kk.getFquantity(), kk.getPacketid(), kk.getFthickness(), kk.getPlannedwidth(),
                kk.getPlannedlength(), kk.getQualitystatus(), kk.getPlannedweight(),
                kk.getPacketstatus(), kk.getEndusertagname()};

            Function<MonthwisePlanTrackerEnduserViewEntity, Object[]> euMapper = kk -> new Object[]{
                kk.getCustomerbatchid(), kk.getMaterialdesc(), kk.getMaterialgrade(),
                kk.getPacketid(), kk.getFthickness(), kk.getPlannedwidth(),
                kk.getPlannedlength(), kk.getPlannedweight(), kk.getPacketstatus(),
                kk.getEndusertagname()};

            addSheet(wb, MONTH_NAMES.get(currentMonth)  + "_Month_PlanTracker",       planHeaders, curList,  planMapper, styles);
            addSheet(wb, MONTH_NAMES.get(previousMonth) + "_Month_PlanTracker",       planHeaders, prevList, planMapper, styles);
            addSheet(wb, MONTH_NAMES.get(previousMonth) + "_EndUserwise_PlanTracker", euHeaders,   euList,   euMapper,   styles);

            boolean hasData = !curList.isEmpty() || !prevList.isEmpty();
            return writeAndAttach(wb, "Monthly_PlanTracker_" + strDate + ".xlsx", hasData, helper);
        } catch (Exception e) {
            log.error("createMonthwisePlanTrackerReport failed partyId={}", partyId, e);
        }
        return false;
    }

    // ── Stock Details Report ──────────────────────────────────────────────────

    @Override
    public boolean createStockDetailsReport(int partyId, String strDate, MimeMessageHelper helper) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            WorkbookStyles                    styles = new WorkbookStyles(wb);
            List<StockDetailsReportViewEntity> list  = stockDetailsReportViewRepository.findByPartyId(partyId);
            addSheet(wb, "StockDetails_Report",
                new Object[]{"Current Date","Finishing Date","EPA Name","EPA location","Mtrl. Age(Days)",
                    "No'of Pieces","Classification Tag","Customer Name","Material Desc","Mother Coil No",
                    "EPA Input Batch","Child Packet Id","MaterialGrade","TDC No","Thickness",
                    "Actual Width","Actual Length","Quality Remarks (For Deviation)","Net Wt (Mt)"},
                list,
                kk -> new Object[]{
                    kk.getCurrentdate(), kk.getFinishingdate(), kk.getEpaname(), kk.getEpalocation(),
                    kk.getCoilage(), kk.getNoofpieces(), kk.getClassificationTag(),
                    kk.getEndusertagname(), kk.getMaterialdesc(), kk.getParentbatch(),
                    kk.getEpainputbatch(), kk.getPacketId(), kk.getMaterialgrade(), kk.getTdcNo(),
                    kk.getFthickness(), kk.getActualwidth(), kk.getActuallength(),
                    kk.getQuality(), kk.getNetweight()},
                styles);
            return writeAndAttach(wb, "StockDetailsReport_" + strDate + ".xlsx", !list.isEmpty(), helper);
        } catch (Exception e) {
            log.error("createStockDetailsReport failed partyId={}", partyId, e);
        }
        return true;
    }

    // ── Monthly Summary Report ────────────────────────────────────────────────

    @Override
    public MonthlySummaryReportEntity getMonthlySummaryReport(List<Integer> partyIdList,
                                                              int currentMonth, int currentYear) {
        MonthlySummaryReportEntity entity = new MonthlySummaryReportEntity();
        try {
            List<Object[]> resultList = monthlySummaryReportRepository.getOpeningStock(partyIdList);
            if (resultList != null && !resultList.isEmpty()) {
                Object[] r = resultList.get(0);
                entity.setCurrentYear(currentYear);
                entity.setCurrentMonth(currentMonth);
                entity.setCurrentStock             (bd(r[0]));
                entity.setRmCrcoil                 (bd(r[1]));  entity.setRmCrsheet          (bd(r[2]));
                entity.setRmHrpocoil               (bd(r[3]));  entity.setRmHr               (bd(r[4]));
                entity.setRmGpcoil                 (bd(r[5]));
                entity.setWipCrcoil                (bd(r[6]));  entity.setWipCrsheet         (bd(r[7]));
                entity.setWipHrpocoil              (bd(r[8]));  entity.setWipHr              (bd(r[9]));
                entity.setWipGpcoil                (bd(r[10]));
                entity.setFgCrcoil                 (bd(r[11])); entity.setFgCrsheet          (bd(r[12]));
                entity.setFgHrpocoil               (bd(r[13])); entity.setFgHr               (bd(r[14]));
                entity.setFgGpcoil                 (bd(r[15]));
                entity.setEdgetrimsCrcoil          (bd(r[16])); entity.setEdgetrimsCrsheet   (bd(r[17]));
                entity.setEdgetrimsHrpocoil        (bd(r[18])); entity.setEdgetrimsHr        (bd(r[19]));
                entity.setEdgetrimsGpcoil          (bd(r[20]));
                entity.setCutendsCrcoil            (bd(r[21])); entity.setCutendsCrsheet     (bd(r[22]));
                entity.setCutendsHrpocoil          (bd(r[23])); entity.setCutendsHr          (bd(r[24]));
                entity.setCutendsGpcoil            (bd(r[25]));
                entity.setDefectiveHandlingCrcoil  (bd(r[26])); entity.setDefectiveHandlingCrsheet  (bd(r[27]));
                entity.setDefectiveHandlingHrpocoil(bd(r[28])); entity.setDefectiveHandlingHr       (bd(r[29]));
                entity.setDefectiveHandlingGpcoil  (bd(r[30]));
                entity.setDefectiveRmCrcoil        (bd(r[31])); entity.setDefectiveRmCrsheet (bd(r[32]));
                entity.setDefectiveRmHrpocoil      (bd(r[33])); entity.setDefectiveRmHr      (bd(r[34]));
                entity.setDefectiveRmGpcoil        (bd(r[35]));
                entity.setCreatedAt(LocalDateTime.now());

                List<Object[]> camList = monthlySummaryReportRepository.getOpeningCamStock(partyIdList);
                if (camList != null && !camList.isEmpty()) {
                    Object[] c = camList.get(0);
                    entity.setRmCamPurushotham         (bd(c[0]));  entity.setRmCamUmesh         (bd(c[1]));
                    entity.setRmCamDivakar             (bd(c[2]));  entity.setRmCamNiraj         (bd(c[3]));
                    entity.setWipCamPurushotham        (bd(c[4]));  entity.setWipCamUmesh        (bd(c[5]));
                    entity.setWipCamDivakar            (bd(c[6]));  entity.setWipCamNiraj        (bd(c[7]));
                    entity.setFgCamPurushotham         (bd(c[8]));  entity.setFgCamUmesh         (bd(c[9]));
                    entity.setFgCamDivakar             (bd(c[10])); entity.setFgCamNiraj         (bd(c[11]));
                    entity.setEdgetrimsCamPurushotham  (bd(c[12])); entity.setEdgetrimsCamUmesh  (bd(c[13]));
                    entity.setEdgetrimsCamDivakar      (bd(c[14])); entity.setEdgetrimsCamNiraj  (bd(c[15]));
                    entity.setCutendsCamPurushotham    (bd(c[16])); entity.setCutendsCamUmesh    (bd(c[17]));
                    entity.setCutendsCamDivakar        (bd(c[18])); entity.setCutendsCamNiraj    (bd(c[19]));
                    entity.setDefhandlingCamPurushotham(bd(c[20])); entity.setDefhandlingCamUmesh(bd(c[21]));
                    entity.setDefhandlingCamDivakar    (bd(c[22])); entity.setDefhandlingCamNiraj(bd(c[23]));
                    entity.setDefrmCamPurushotham      (bd(c[24])); entity.setDefrmCamUmesh      (bd(c[25]));
                    entity.setDefrmCamDivakar          (bd(c[26])); entity.setDefrmCamNiraj      (bd(c[27]));
                }
                monthlySummaryReportRepository.save(entity);
            }
        } catch (Exception e) {
            log.error("Error at getMonthlySummaryReport: ", e);
        }
        return entity;
    }

    private static BigDecimal bd(Object val) {
        return val != null ? new BigDecimal(val.toString()) : BigDecimal.ZERO;
    }
}