package com.steel.product.application.service;

import com.steel.product.application.dao.FGReportViewRepository;
import com.steel.product.application.dao.StockReportViewRepository;
import com.steel.product.application.dao.WIPReportViewRepository;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.FGReportViewEntity;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.StockReportViewEntity;
import com.steel.product.application.entity.WIPReportViewEntity;
import com.steel.product.application.util.CSVUtil;
import com.steel.product.application.util.EmailUtil;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    private final InwardEntryService inwardEntryService;

	@Autowired
	StockReportViewRepository stockReportViewRepository;
	
	@Autowired
	FGReportViewRepository fgReportViewRepository;
	
	@Autowired
	WIPReportViewRepository wipReportViewRepository;
	
    private final InstructionService instructionService;
    private final CSVUtil csvUtil;
    private final EmailUtil emailUtil;

    @Value("#{'${stock.report.headers}'.split(',')}")
    private List<String> stockReportHeaders;

	@Autowired 
	Environment env;
	
    @Autowired
    public ReportsServiceImpl(InwardEntryService inwardEntryService, InstructionService instructionService, CSVUtil csvUtil, EmailUtil emailUtil) {
        this.inwardEntryService = inwardEntryService;
        this.instructionService = instructionService;
        this.csvUtil = csvUtil;
        this.emailUtil = emailUtil;
    }

    @Override
    public String generateAndMailStockReport(StockReportRequest stockReportRequest) {
        LOGGER.info("inside generateAndMailStockReport method");
        List<InwardEntry> inwardEntries = inwardEntryService.findInwardByPartyId(stockReportRequest.getPartyId());
        if(inwardEntries.isEmpty()){
            throw new RuntimeException("No inwards found for party id "+stockReportRequest.getPartyId());
        }
        HashMap<Integer,Double> unprocessedWeights = instructionService.findSumOfPlannedWeightAndActualWeightForUnprocessed();
        LOGGER.info("unprocessed weights "+unprocessedWeights.size());
        String email = inwardEntries.get(0).getParty().getEmail1();
        String[] headers = stockReportHeaders.toArray(new String[0]);
        File report = csvUtil.generateStockReportCSV(headers,inwardEntries,unprocessedWeights);
        if(report == null){
            return "error in generating csv";
        }
        emailUtil.sendEmail(report,email);
        if(report.exists()) {
            report.delete();
        }
        return "email sent ok !!";
    }
    
	@Override
	public boolean createStockReport(int partyId, String strDate, MimeMessageHelper helper ) {

		boolean attachmentRequired=true;
		try {
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			// Create a blank sheet
			XSSFSheet spreadsheet = workbook.createSheet("Stock_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getStockReportDetails(partyId);

			// Iterate over data and write to sheet
			Set<String> keyid = acctStatementMap.keySet();
			int rowid = 0;

			for (String key : keyid) {
				row = spreadsheet.createRow(rowid++);
				Object[] objectArr = acctStatementMap.get(key);
				int cellid = 0;

				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
					cell.setCellValue((String) obj);
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            //System.out.println("folderpath -- "+baseDirectory);
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"StockReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("StockReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getStockReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<StockReportViewEntity> partyList = stockReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1", new Object[] { "CoilNumber", "CustomerBatchId", "MaterialDesc", "MaterialGrade", "Thickness", "Width", "Length", "NetWeight", "UnprocessedWeight", "InStockWeight", "InwardStatus" });

			int cnt = 1;
			for (StockReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getMaterialDesc(),
								kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(), kk.getFlength(),
								kk.getNetWeight(), kk.getUnProcessedWeight(), kk.getInStockWeight(),kk.getInwardStatus()});
			}
		} catch (Exception e) {
			LOGGER.error("Error at getStockReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired=true;
		try {
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			// Create a blank sheet
			XSSFSheet spreadsheet = workbook.createSheet("FG_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getFGReportDetails(partyId);

			// Iterate over data and write to sheet
			Set<String> keyid = acctStatementMap.keySet();
			int rowid = 0;

			for (String key : keyid) {
				row = spreadsheet.createRow(rowid++);
				Object[] objectArr = acctStatementMap.get(key);
				int cellid = 0;

				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
					cell.setCellValue((String) obj);
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"FGReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("FGReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getFGReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<FGReportViewEntity> partyList = fgReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "MaterialDesc", "MaterialGrade",
							"Thickness", "Width", "Length", "FG Weight", 
							"Packet Id", "Thickness", "Planned Width", "Planned Length",
							"Planned Weight", "Actual Weight", "Process Status", "Instruction Status",
							"Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getMaterialDesc(),kk.getMaterialGrade(),
								 kk.getFthickness(), kk.getFwidth(), kk.getFlength(),kk.getFgWeight(), 
								kk.getPacketId(), kk.getThickness(), kk.getPlannedWidth(),kk.getPlannedLength(), 
								kk.getPlannedWeight(), kk.getActualWeight() , kk.getProcessStatus(),kk.getInstructionStatus(), 
								kk.getClassificationTag(), kk.getEnduserTagName() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getFGReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public boolean createWIPReport(int partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired=true;
		try {
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			// Create a blank sheet
			XSSFSheet spreadsheet = workbook.createSheet("WIP_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getWIPReportDetails(partyId);

			// Iterate over data and write to sheet
			Set<String> keyid = acctStatementMap.keySet();
			int rowid = 0;

			for (String key : keyid) {
				row = spreadsheet.createRow(rowid++);
				Object[] objectArr = acctStatementMap.get(key);
				int cellid = 0;

				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
					cell.setCellValue((String) obj);
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"WIPReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("WIPReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getWIPReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<WIPReportViewEntity> partyList = wipReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "MaterialDesc", "MaterialGrade", "Thickness",
							"Width", "Length", "Net Weight", "In Stock Weight", "WIP Weight", "Thickness",
							"Planned Width", "Planned Length", "Planned Weight", "Inward Status" });

			int cnt = 1;
			for (WIPReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt, new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(),
						kk.getMaterialDesc(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
						kk.getFlength(), kk.getNetWeight(), kk.getInStockWeight(), kk.getWipWeight(), kk.getThickness(),
						kk.getPlannedWidth(), kk.getPlannedLength(), kk.getPlannedWeight(), kk.getInwardStatus() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getWIPReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

}
