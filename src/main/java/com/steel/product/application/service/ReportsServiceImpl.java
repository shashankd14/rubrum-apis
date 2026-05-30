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

@Service
public class ReportsServiceImpl implements ReportsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    private final InwardEntryService inwardEntryService;
    
	@Autowired
	OutwardReportViewRepository outwardReportViewRepository;

	@Autowired
	InwardReportViewRepository inwardReportViewRepository;

	@Autowired
	MonthlySummaryReportRepository monthlySummaryReportRepository;

	@Autowired
	StockReportViewRepository stockReportViewRepository;

	@Autowired
	StockDetailsReportViewRepository stockDetailsReportViewRepository;

	@Autowired
	FGReportViewRepository fgReportViewRepository;
	
	@Autowired
	MonthwisePlanTrackerViewRepository monthwisePlanTrackerViewRepository;
	
	@Autowired
	MonthwisePlanTrackerEndUserwiseViewRepository endUserwiseViewRepository;
	
	@Autowired
	WIPReportViewRepository wipReportViewRepository;
	
	@Autowired
	RMReportViewRepository rmReportViewRepository;
	
	@Autowired
	StockSummaryReportViewRepository stockSummaryReportViewRepository;
    
	@Autowired ProcessingReportViewRepository processingRepository;
	
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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
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

			acctStatementMap.put("1",
			new Object[] { "CoilNumber", "CustomerBatchId", "Coil Age", "MaterialDesc", "MaterialGrade",
					"Thickness", "Width", "Length", "NetWeight", "UnprocessedWeight", "InStockWeight",
					"Remarks", "InwardStatus" });

			int cnt = 1;
			for (StockReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
				new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getCoilage(),
						kk.getMaterialDesc(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
						kk.getFlength(), kk.getNetWeight(), kk.getUnProcessedWeight(), kk.getInStockWeight(),
						kk.getRemarks(), kk.getInwardStatus() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getStockReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	@Override
	public boolean createFGReport(int partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired = false;
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
			XSSFSheet fgSpreadsheet = workbook.createSheet("FG_Classification");
			XSSFSheet othersSpreadsheet = workbook.createSheet("Others_Classification");
			XSSFSheet qualityDefectsSpreadsheet = workbook.createSheet("Quality_Defects");

			// Create row object
			XSSFRow row;

			List<FGReportViewEntity> fgReportDetailsList = getFGReportDetails(partyId);
			Map<String, Object[]> fgAcctStatementMap = getFGCassificationDetails(fgReportDetailsList);
			Map<String, Object[]> othersActStatementMap = getOthersCassificationDetails(fgReportDetailsList);
			Map<String, Object[]> qualityDefectsActStatementMap = getQualityDefectsClassificationDetails(fgReportDetailsList);

			// Iterate over data and write to sheet
			Set<String> keyid = fgAcctStatementMap.keySet();
			int rowid = 0;

			for (String key : keyid) {
				row = fgSpreadsheet.createRow(rowid++);
				Object[] objectArr = fgAcctStatementMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}

			// Iterate over data and write to sheet
			Set<String> keyid1 = othersActStatementMap.keySet();
			rowid = 0;

			for (String key : keyid1) {
				row = othersSpreadsheet.createRow(rowid++);
				Object[] objectArr = othersActStatementMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}

			// Iterate over data and write to sheet
			Set<String> keyid2 = qualityDefectsActStatementMap.keySet();
			rowid = 0;

			for (String key : keyid2) {
				row = qualityDefectsSpreadsheet.createRow(rowid++);
				Object[] objectArr = qualityDefectsActStatementMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"FGReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(fgAcctStatementMap!=null && fgAcctStatementMap.size()>1) {
				attachmentRequired=true;
				helper.addAttachment("FGReport_" + strDate + ".xlsx", file);
			}
			if(othersActStatementMap!=null && othersActStatementMap.size()>1) {
				attachmentRequired=true;
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

	public List<FGReportViewEntity> getFGReportDetails(int partyId) {
		List<FGReportViewEntity> partyList = fgReportViewRepository.findByPartyId(partyId);
		return partyList;
	}

	public Map<String, Object[]> getFGCassificationDetails(List<FGReportViewEntity> partyList ) {
		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {

			acctStatementMap.put("1",
			new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","Current Date","Coil Age(No'of Days)",
					"No'of Pieces","MaterialDesc", "MaterialGrade", "TDC No","Remarks", "Packet Id", "Thickness", "Actual Width",
					"Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });
			acctStatementMap.put("1",
			new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","Current Date","Coil Age(No'of Days)",
					"No'of Pieces","MaterialDesc", "MaterialGrade", "TDC No","Remarks","Packet Id", "Thickness", "Actual Width",
					 "Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				if ("FG".equals(kk.getClassificationTag())) {
					cnt++;
					acctStatementMap.put("" + cnt,
					new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getFinishingDate(),
					kk.getCurrentdate(), kk.getCoilage(), kk.getNoofpieces(),kk.getMaterialDesc(), kk.getMaterialGrade(),
					kk.getTdcNo(), kk.getRemarks(), kk.getPacketId(), kk.getThickness(), kk.getActualwidth(),
					kk.getActuallength(), kk.getActualweight(), kk.getClassificationTag(),
					((kk.getEnduserTagName() != null && kk.getEnduserTagName().length() > 0) ? kk.getEnduserTagName() : "") });
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getFGCassificationDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	public Map<String, Object[]> getOthersCassificationDetails(List<FGReportViewEntity> partyList ) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {

			Set<String> classificationSet = new HashSet<>(Arrays.asList("EDGE TRIM", "CUT ENDS", "WIP(NCO)", "WIP(FG)",
					"WIP(END CUT)", "WIP(EDGE TRIM)", "WIP(CUT ENDS)", "WIP (SFCP)", "NCO"));

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","Current Date","Coil Age(No'of Days)",
							"No'of Pieces","MaterialDesc", "MaterialGrade", "TDC No","Remarks","Packet Id", "Thickness",
							"Actual Width", "Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				String tag = kk.getClassificationTag();
				if (tag != null && classificationSet.contains(tag.trim().toUpperCase())) {
					cnt++;
					acctStatementMap.put("" + cnt, new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(),
					kk.getFinishingDate(), kk.getCurrentdate(), kk.getCoilage(),kk.getNoofpieces(), kk.getMaterialDesc(),  
					kk.getMaterialGrade(), kk.getTdcNo(), kk.getRemarks(), kk.getPacketId(), kk.getThickness(),
					kk.getActualwidth(), kk.getActuallength(), kk.getActualweight(), kk.getClassificationTag(),
					((kk.getEnduserTagName() != null && kk.getEnduserTagName().length() > 0) ? kk.getEnduserTagName() : "") });
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getOthersCassificationDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	public Map<String, Object[]> getQualityDefectsClassificationDetails(List<FGReportViewEntity> partyList ) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {

			Set<String> classificationSet = new HashSet<>(Arrays.asList("EDGE TRIM", "FG", "CUT ENDS", "WIP(NCO)", "WIP(FG)",
					"WIP(END CUT)", "WIP(EDGE TRIM)", "WIP(CUT ENDS)", "WIP (SFCP)", "NCO"));

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","Current Date","Coil Age(No'of Days)",
							"No'of Pieces","MaterialDesc", "MaterialGrade", "TDC No","Remarks","Packet Id", "Thickness",
							"Actual Width", "Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				String tag = kk.getClassificationTag();
				if (tag != null && !classificationSet.contains(tag.trim().toUpperCase())) {
					cnt++;
					acctStatementMap.put("" + cnt, new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(),
					kk.getFinishingDate(), kk.getCurrentdate(), kk.getCoilage(),kk.getNoofpieces(), kk.getMaterialDesc(),  
					kk.getMaterialGrade(), kk.getTdcNo(), kk.getRemarks(), kk.getPacketId(), kk.getThickness(),
					kk.getActualwidth(), kk.getActuallength(), kk.getActualweight(), kk.getClassificationTag(),
					((kk.getEnduserTagName() != null && kk.getEnduserTagName().length() > 0) ? kk.getEnduserTagName() : "") });
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getOthersCassificationDetails " + e.getMessage());
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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
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
					new Object[] { "CoilNumber", "CustomerBatchId", 
							"Processing Plan Date","Current Date","Coil Age(No'of Days)",
							"MaterialDesc", "MaterialGrade", "Thickness",
							"Width", "Length", "Net Weight", "In Stock Weight", "WIP Weight", "Remarks", "Packet id","Thickness",
							"Planned Width", "Planned Length", "Planned Weight", "Inward Status", "Classification Tag",
							"End User Tag" });

			int cnt = 1;
			for (WIPReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt, new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(),
						kk.getProcessingPlanDate(), kk.getCurrentdate(), kk.getCoilage(),
						kk.getMaterialDesc(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
						kk.getFlength(), kk.getNetWeight(), kk.getInStockWeight(), kk.getWipWeight(), kk.getRemarks(),
						kk.getPacketId(), kk.getThickness(), kk.getPlannedWidth(), kk.getPlannedLength(), kk.getPlannedWeight(), 
						kk.getInwardStatus(), kk.getClassificationTag(), kk.getEnduserTagName() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getWIPReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public boolean createWIPReportEndusertagwise(Integer partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired = false;
		try {
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			List<WIPReportViewEntity> wipReportDetailsList = getWIPReportDetails1(partyId);
			
			Map<String, String> listOfEndUserTags =  getListOfWIPEndUserTags(wipReportDetailsList);
			for (Map.Entry<String, String> entry : listOfEndUserTags.entrySet()) {
				// Create a blank sheet
				XSSFSheet fgSpreadsheet = workbook.createSheet(entry.getKey()+"_EndUserTag");

				// Create row object
				XSSFRow row;
				
				Map<String, Object[]> fgAcctStatementMap = getEndUserWiseWIPDetails(wipReportDetailsList, entry.getKey());
				// Iterate over data and write to sheet
				Set<String> keyid = fgAcctStatementMap.keySet();
				int rowid = 0;

				for (String key : keyid) {
					row = fgSpreadsheet.createRow(rowid++);
					Object[] objectArr = fgAcctStatementMap.get(key);
					int cellid = 0;
					for (Object obj : objectArr) {
						Cell cell = row.createCell(cellid++);
					    cell.setCellStyle(borderStyle);
						if (obj != null) {
							cell.setCellValue("" + obj);
						} else {
							cell.setCellValue("");
						}
						
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"WIP_EndUserTagwise_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			helper.addAttachment("WIP_EndUserTagwise_" + strDate + ".xlsx", file);
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}
	
	public Map<String, String> getListOfWIPEndUserTags(List<WIPReportViewEntity> partyList) {
		Map<String, String> listOfEndUserTags = new LinkedHashMap<>();
		try {
			for (WIPReportViewEntity kk : partyList) {
				if (kk.getEnduserTagName() != null && kk.getEnduserTagName().length() > 0) {
					listOfEndUserTags.put(kk.getEnduserTagName(), kk.getEnduserTagName());
				} else {
					listOfEndUserTags.put("NO_ENDUSERTAG", "NO_ENDUSERTAG");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getListOfWIPEndUserTags " + e.getMessage());
		}
		return listOfEndUserTags;
	}
	
	public List<WIPReportViewEntity> getWIPReportDetails1(int partyId) {

		List<WIPReportViewEntity> partyList = wipReportViewRepository.findByPartyId(partyId);
		return partyList;
	}

	public Map<String, Object[]> getEndUserWiseWIPDetails(List<WIPReportViewEntity> partyList, String endUserTagName) {
		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();
		try {

			acctStatementMap.put("1", new Object[] { "CoilNumber", "CustomerBatchId", "Processing Plan Date", "Current Date",
				"Coil Age(No'of Days)", "MaterialDesc", "MaterialGrade", "Thickness", "Width", "Length",
				"Net Weight", "In Stock Weight", "WIP Weight", "Remarks", "Packet id", "Thickness",
				"Planned Width", "Planned Length", "Planned Weight", "Inward Status", "Classification Tag", "End User Tag"});

			int cnt = 1;
			for (WIPReportViewEntity kk : partyList) {
				if (kk.getEnduserTagName() == null || "".equals(kk.getEnduserTagName())) {
					kk.setEnduserTagName("NO_ENDUSERTAG");
				}
				if (endUserTagName.equals(kk.getEnduserTagName())) {
					cnt++;
					acctStatementMap.put("" + cnt,
					new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getProcessingPlanDate(),
					kk.getCurrentdate(), kk.getCoilage(), kk.getMaterialDesc(), kk.getMaterialGrade(),
					kk.getFthickness(), kk.getFwidth(), kk.getFlength(), kk.getNetWeight(),
					kk.getInStockWeight(), kk.getWipWeight(), kk.getRemarks(), kk.getPacketId(),
					kk.getThickness(), kk.getPlannedWidth(), kk.getPlannedLength(),
					kk.getPlannedWeight(), kk.getInwardStatus(), kk.getClassificationTag(), 
					kk.getEnduserTagName()});
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error at getEndUserWiseWIPDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	@Override
	public boolean createStockSummaryReport(int partyId, String strDate, MimeMessageHelper helper) {

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
			XSSFSheet spreadsheet = workbook.createSheet("StockSummary_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getStockSummaryReportDetails(partyId);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"StockSummaryReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("StockSummaryReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getStockSummaryReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<StockSummaryReportViewEntity> partyList = stockSummaryReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1",
					new Object[] { "Coil No", "Batch No", "MaterialDesc", "MaterialGrade", "Thickness", "Width",
							"Length", "NetWeight", "InStockWeight", "FG Qty", "FG_Classification",
							"CUT-ENDS_Classification", "EDGE-TRIM_Classification", "OTHERS_Classification",
							"WIP_Classification", "BLANK_Classification", "Quality Defects", "UnprocessedWeight", "WIP Qty",
							"Dispatched Qty","Remarks", "InwardStatus" });

			int cnt = 1;
			for (StockSummaryReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getMaterialDesc(),
								kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(), kk.getFlength(),
								kk.getNetweight(), kk.getInstockweight(), kk.getFgqty(), kk.getFgclassification(),
								kk.getCutendsclassification(), kk.getEdgetrimclassification(),
								kk.getOthersclassification(), kk.getWipclassification(), kk.getBlankclassification(),
								kk.getQualitydefects(), kk.getUnprocessedweight(), kk.getWipqty(),
								kk.getDispatchedweight(), kk.getRemarks(), kk.getInwardstatus() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at createStockSummaryReport " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public boolean createRMReport(int partyId, String strDate, MimeMessageHelper helper) {

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
			XSSFSheet spreadsheet = workbook.createSheet("RM_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getRMReportDetails(partyId);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"RMReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("RMReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getRMReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<RMReportViewEntity> partyList = rmReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "Mother Coil No", "Received Date","Current Date","Coil Age(No'of Days)",
							"MaterialDesc", "MaterialGrade", "Thickness", "Width", "Length", "Net Weight", 
							"Customer Invoice Number", "Customer Invoice Date", "Status", "Created On", "Remarks" });

			int cnt = 1;
			for (RMReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
				new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getParentcoilnumber(), kk.getReceivedDate(),
						kk.getCurrentdate(), kk.getCoilage(), kk.getDescription(), kk.getMaterialGrade(),
						kk.getFthickness(), kk.getFwidth(), kk.getFlength(), kk.getNetWeight(),
						kk.getCustInvNo(), kk.getCustInvDate(), kk.getInwardStatus(), kk.getCreatedOn(),
						kk.getRemarks() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getWIPReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public List<StockSummaryReportViewEntity> reconcileReport(String coilNumber) {
		return stockSummaryReportViewRepository.findByCoilNumber(coilNumber);
	}

	@Override
	public boolean createInwardMonthlyReport(Integer partyId, MimeMessageHelper helper,
			Integer month, Map<Integer, String> months, Integer year) {

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
			XSSFSheet spreadsheet = workbook.createSheet("Inward_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getMonthlyInwardReportDetails(partyId, month, year);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            //System.out.println("folderpath -- "+baseDirectory);
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"InwardReport_"+months.get(month)+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("InwardReport_"+months.get(month)+".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	@Override
	public boolean createOutwardMonthlyReport(Integer partyId, MimeMessageHelper helper,
			Integer month, Map<Integer, String> months, Integer year) {

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
			XSSFSheet spreadsheet = workbook.createSheet("Outward_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getMonthlyOutwardReportDetails(partyId, month, year);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            //System.out.println("folderpath -- "+baseDirectory);
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"OutwardReport_"+months.get(month)+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("OutwardReport_"+months.get(month)+".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	@Override
	public boolean createStockMonthlyReport(Integer partyId, MimeMessageHelper helper, Integer month,
			Map<Integer, String> months) {

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

			Map<String, Object[]> acctStatementMap = getMonthlyStockReportDetails(partyId, month);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            //System.out.println("folderpath -- "+baseDirectory);
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"StockReport_"+months.get(month)+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("StockReport_"+months.get(month)+".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}
	
	public Map<String, Object[]> getMonthlyInwardReportDetails(Integer partyId, Integer month, Integer year) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<InwardReportViewEntity> partyList = inwardReportViewRepository.findByPartyIdAndMnthAndYer(partyId, month, year);

			acctStatementMap.put("1",
					new Object[] { "CustomerName", "CoilNumber", "CustomerBatchId", "ReceivedDate", "MaterialDesc",
							"MaterialGrade", "TDC No", "Thickness", "Width", "Length", "NetWeight", "customerinvoiceno",
							"customerinvoicedate", "InwardStatus" });

			int cnt = 1;
			for (InwardReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCustomerName(), kk.getCoilnumber(), kk.getCustomerbatchid(),
								kk.getReceivedDate(), kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getTdcNo(),
								kk.getFthickness(), kk.getFwidth(), kk.getFlength(), kk.getNetWeight(),
								kk.getCustomerinvoiceno(), kk.getCustomerinvoicedate(), kk.getInwardStatus() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getInwardReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	public Map<String, Object[]> getMonthlyOutwardReportDetails(Integer partyId, Integer month, Integer year) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<OutwardReportViewEntity> partyList = outwardReportViewRepository.findByPartyIdAndMnthAndYer(partyId, month, year);
			acctStatementMap.put("1",
					new Object[] {"S.No", "Aspen DC No", "customerbatchid", "material description", "vehicleno", "customer name", "sap invoice No", "sap invoice date",
							"material grade", "tdc no", "process type", "Qty ", 
							"Base Rate", 
							"Packing Charges", 
							"Lamination Charges", 
							"Additional Charges", 
							"Rate", 
							"Total Amount"});
			int cnt = 1;
			int oldDCNo=0;
			BigDecimal totalweight = new BigDecimal("0.00");
			for (OutwardReportViewEntity kk : partyList) {
				if (oldDCNo > 0 && oldDCNo != kk.getAspendcno()) {
					cnt++;
					acctStatementMap.put("" + cnt, new Object[] { cnt, "", "", "", "", "", "", "", "", "", "",
							totalweight.divide(new BigDecimal("1000")).setScale(3, RoundingMode.HALF_UP),
							"", "", "", "", "", "", });
				}
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { cnt, kk.getAspendcno(), kk.getCustomerbatchid(),kk.getMaterialgrade(),
						kk.getVehicleno(), kk.getCustomername(), kk.getSapinvoiceno(), kk.getSapinvoicedate(),
						kk.getMaterialdesc(), kk.getTdcNo(), kk.getProcessname(), 
						kk.getQty().divide(new BigDecimal("1000")).setScale(3, RoundingMode.HALF_UP),
						kk.getBasePrice(), 
						kk.getPackingCharges(),
						kk.getLaminationCharges(), 
						kk.getAdditionalCharges(), 
						kk.getRate(), 
						kk.getTotalprice().setScale(2, RoundingMode.HALF_UP)
						});
				oldDCNo = kk.getAspendcno();
				totalweight =  kk.getTotalweight();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error at getMonthlyOutwardReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	public Map<String, Object[]> getMonthlyStockReportDetails(Integer partyId, Integer month) {

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
	public boolean createProcessingMonthlyReport(Integer partyId, MimeMessageHelper helper,
			Integer month, Map<Integer, String> months, Integer year) {

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
			XSSFSheet spreadsheet = workbook.createSheet("Processing_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getMonthlyProcessingReportDetails(partyId, month, year);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"ProcessingReport_"+months.get(month)+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("ProcessingReport_"+months.get(month)+".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getMonthlyProcessingReportDetails(Integer partyId, Integer month, Integer year) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<ProcessingReportViewEntity> partyList = processingRepository.findByPartyIdAndProcessmonthAndProcessyear(partyId, month, year);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "CustomerName", "ProcessName","MaterialDesc",
							"MaterialGrade", "ProcessDate", "PacketId", "Packet Thickness", "Packet Width", "Packet Length",
							"Finishing Weight", "Finishing Date", "End User Tag"});
			int cnt = 1;
			for (ProcessingReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilnumber(), kk.getCustomerbatchid(), kk.getCustomerName(),
								kk.getProcessName(), kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getProcessdate(),
								kk.getPacketId(), kk.getPacketThickness(),
								(kk.getPacketWidth() == null ? "" : kk.getPacketWidth()),
								(kk.getPacketLength() == null ? "" : kk.getPacketLength()),
								(kk.getFinishingWeight() == null ? "" : kk.getFinishingWeight()),
								(kk.getFinishingWeight() == null ? "" : kk.getFinishingDate()),
								(kk.getEnduser_tag_name() == null ? "" : kk.getEnduser_tag_name()) });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getMonthlyProcessingReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	@Override
	public boolean createFinishingMonthlyReport(Integer partyId, MimeMessageHelper helper,
			Integer month, Map<Integer, String> months, Integer year) {

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
			XSSFSheet spreadsheet = workbook.createSheet("Finishing_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getMonthlyFinishingReportDetails(partyId, month, year);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"FinishingReport_"+months.get(month)+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("FinishingReport_"+months.get(month)+".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getMonthlyFinishingReportDetails(Integer partyId, Integer month, Integer year) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<ProcessingReportViewEntity> partyList = processingRepository.findByPartyIdAndFinishmonthAndFinishyear(partyId, month, year);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "CustomerName", "ProcessName","MaterialDesc",
							"MaterialGrade", "ProcessDate", "PacketId", "Packet Thickness", "Packet Width", "Packet Length",
							"Finishing Weight", "Finishing Date", "End User Tag"});
			int cnt = 1;
			for (ProcessingReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilnumber(), kk.getCustomerbatchid(), kk.getCustomerName(),
								kk.getProcessName(), kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getProcessdate(),
								kk.getPacketId(), kk.getPacketThickness(),
								(kk.getPacketWidth() == null ? "" : kk.getPacketWidth()),
								(kk.getPacketLength() == null ? "" : kk.getPacketLength()),
								(kk.getFinishingWeight() == null ? "" : kk.getFinishingWeight()),
								(kk.getFinishingWeight() == null ? "" : kk.getFinishingDate()),
								(kk.getEnduser_tag_name() == null ? "" : kk.getEnduser_tag_name()) });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getMonthlyFinishingReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	@Override
	public boolean createEndUserTagWiseFGReport(Integer partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired = false;
		try {
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			List<FGReportViewEntity> fgReportDetailsListDummy =getFGReportDetails(partyId);
			List<FGReportViewEntity> fgClassificationList = new ArrayList<>();
			
			for (FGReportViewEntity kk : fgReportDetailsListDummy) {
				if("FG".equals(kk.getClassificationTag())) {
					fgClassificationList.add(kk);
				}
			}
			
			Map<String, String> listOfEndUserTags =  getListOfEndUserTags(fgClassificationList);
			for (Map.Entry<String, String> entry : listOfEndUserTags.entrySet()) {
				// Create a blank sheet
				XSSFSheet fgSpreadsheet = workbook.createSheet(entry.getKey()+"_EndUserTag");

				// Create row object
				XSSFRow row;
				
				Map<String, Object[]> fgAcctStatementMap = getEndUserWiseFGDetails(fgClassificationList, entry.getKey());
				// Iterate over data and write to sheet
				Set<String> keyid = fgAcctStatementMap.keySet();
				int rowid = 0;

				for (String key : keyid) {
					row = fgSpreadsheet.createRow(rowid++);
					Object[] objectArr = fgAcctStatementMap.get(key);
					int cellid = 0;
					for (Object obj : objectArr) {
						Cell cell = row.createCell(cellid++);
					    cell.setCellStyle(borderStyle);
						if (obj != null) {
							cell.setCellValue("" + obj);
						} else {
							cell.setCellValue("");
						}
						
					}
				}
			}
			
			Map<String, Object[]> othersActStatementMap = getOthersCassificationDetails(fgReportDetailsListDummy);
			// Iterate over data and write to sheet
			Set<String> keyid1 = othersActStatementMap.keySet();
			int rowid = 0;
			XSSFSheet othersSpreadsheet = workbook.createSheet("Others_Classification");

			for (String key : keyid1) {
				XSSFRow row = othersSpreadsheet.createRow(rowid++);
				Object[] objectArr = othersActStatementMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"FG_EndUserTagWise_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			helper.addAttachment("FG_EndUserTagWise_" + strDate + ".xlsx", file);
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, String> getListOfEndUserTags(List<FGReportViewEntity> partyList) {
		Map<String, String> listOfEndUserTags = new LinkedHashMap<>();
		try {
			for (FGReportViewEntity kk : partyList) {
				if (kk.getEnduserTagName() != null && kk.getEnduserTagName().length() > 0) {
					listOfEndUserTags.put(kk.getEnduserTagName(), kk.getEnduserTagName());
				} else {
					listOfEndUserTags.put("NO_ENDUSERTAG", "NO_ENDUSERTAG");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getListOfEndUserTags " + e.getMessage());
		}
		return listOfEndUserTags;
	}

	public Map<String, Object[]> getEndUserWiseFGDetails(List<FGReportViewEntity> partyList, String endUserTagName) {
		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();
		try {

			acctStatementMap.put("1",
			new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date", "Current Date",
					"Coil Age(No'of Days)", "No'of Pieces", "MaterialDesc", "MaterialGrade", "Remarks",
					"Packet Id", "Thickness", "Actual Width", "Actual Length", "Actual Weight",
					"Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				if (kk.getEnduserTagName() == null || "".equals(kk.getEnduserTagName())) {
					kk.setEnduserTagName("NO_ENDUSERTAG");
				}
				if (endUserTagName.equals(kk.getEnduserTagName())) {
					cnt++;
					acctStatementMap.put("" + cnt,
					new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getFinishingDate(),
						kk.getCurrentdate(), kk.getCoilage(), kk.getNoofpieces(), kk.getMaterialGrade(),
						kk.getRemarks(), kk.getPacketId(), kk.getThickness(), kk.getActualwidth(), kk.getActuallength(), 
						kk.getActualweight(), kk.getClassificationTag(), ("NO_ENDUSERTAG".equals(kk.getEnduserTagName()) ? "" : kk.getEnduserTagName()) });
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getEndUserWiseFGDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public boolean createMonthwisePlanTrackerReport(int partyId, String strDate, MimeMessageHelper helper) {

		boolean attachmentRequired = false;
		try {
			
			Map<Integer, String> monthNames =new HashMap<>();
			monthNames.put(1, "Jan");
			monthNames.put(2, "Feb");
			monthNames.put(3, "Mar");
			monthNames.put(4, "Apr");
			monthNames.put(5, "May");
			monthNames.put(6, "Jun");
			monthNames.put(7, "Jul");
			monthNames.put(8, "Aug");
			monthNames.put(9, "Sep");
			monthNames.put(10, "Oct");
			monthNames.put(11, "Nov");
			monthNames.put(12, "Dec");
			
			LocalDate currentDate = LocalDate.now();
			Integer currentMonth = currentDate.getMonthValue();
			Integer previousMonth = ( currentMonth-1);
			Integer currentYear = currentDate.getYear();
			Integer currentDay = currentDate.getDayOfMonth();
			//LOGGER.info("currentYear  == " + currentYear);
			//LOGGER.info("previousMonth  == " + previousMonth);
			//LOGGER.info("currentMonth  == " + currentMonth);
			//LOGGER.info("currentDay  == " + currentDay);
			
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
		    borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			
			// Create a blank sheet
			XSSFSheet currentMonthSpreadsheet = workbook.createSheet(monthNames.get(currentMonth)+"_Month_PlanTracker");
			XSSFSheet previuosMonthSpreadsheet = workbook.createSheet(monthNames.get(previousMonth)+"_Month_PlanTracker");
			XSSFSheet enduserwiseSpreadsheet = workbook.createSheet(monthNames.get(previousMonth)+"_EndUserwise_PlanTracker");

			// Create row object
			XSSFRow row;
			
			Map<String, Object[]> currentMonthMap = null;
			Map<String, Object[]> previuosMonthMap = null;
			Map<String, Object[]> enduserwiseMap = null;
			currentMonthMap = getMonthwisePlanTrackerDetails(partyId, currentMonth, currentYear);
			enduserwiseMap = getEnsUserTagwisePlanTrackerDetails(partyId, previousMonth, currentYear);
			if(currentDay <= 10) {
				previuosMonthMap = getMonthwisePlanTrackerDetails(partyId, previousMonth, currentYear);
 			}
			
			// Iterate over data and write to sheet
			Set<String> keyid = currentMonthMap.keySet();
			int rowid = 0;

			for (String key : keyid) {
				row = currentMonthSpreadsheet.createRow(rowid++);
				Object[] objectArr = currentMonthMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
				    cell.setCellStyle(borderStyle);
				    if(obj!=null ) {
						cell.setCellValue(""+obj);
				    } else {
						cell.setCellValue("");
				    }
				}
			}
			if(previuosMonthMap!=null && previuosMonthMap.size()>1) {
				// Iterate over data and write to sheet
				Set<String> keyid1 = previuosMonthMap.keySet();
				rowid = 0;
	
				for (String key : keyid1) {
					row = previuosMonthSpreadsheet.createRow(rowid++);
					Object[] objectArr = previuosMonthMap.get(key);
					int cellid = 0;
					for (Object obj : objectArr) {
						Cell cell = row.createCell(cellid++);
						cell.setCellStyle(borderStyle);
						if (obj != null) {
							cell.setCellValue("" + obj);
						} else {
							cell.setCellValue("");
						}
					}
				}
			}

			// Iterate over data and write to sheet
			Set<String> keyid2 = enduserwiseMap.keySet();
			rowid = 0;

			for (String key : keyid2) {
				row = enduserwiseSpreadsheet.createRow(rowid++);
				Object[] objectArr = enduserwiseMap.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
					cell.setCellStyle(borderStyle);
					if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			 
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"Monthly_PlanTracker_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(currentMonthMap!=null && currentMonthMap.size()>1) {
				attachmentRequired=true;
				helper.addAttachment("Monthly_PlanTracker_" + strDate + ".xlsx", file);
			}
			if(previuosMonthMap!=null && previuosMonthMap.size()>1) {
				attachmentRequired=true;
				helper.addAttachment("Monthly_PlanTracker_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getMonthwisePlanTrackerDetails(int partyId, int month, int year) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<MonthwisePlanTrackerViewEntity> partyList = monthwisePlanTrackerViewRepository
					.findByPartyidAndMnthAndYer(partyId, month, year);

			acctStatementMap.put("1",
					new Object[] { "Plan pdf No", "Plan Date", "Finishing Date", "Mother Coil No", "Plan pdf qty", "Batch No",
							"Aspen Coil No", "Material Type", "Material Grade", "Inward Coil Weight", "Packet Id",
							"Thickness", "Width", "Length", "Quality Status", "Packet Qty",
							"Packet Status", "End User" });
			int cnt = 1;
			for (MonthwisePlanTrackerViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getPartdetailsid(), kk.getPlandate(), kk.getFinishingdate(), kk.getMothercoilno(),
								kk.getPlanPdfQty(), kk.getCustomerbatchid(), kk.getAspencoilno(), kk.getMaterialdesc(),
								kk.getMaterialgrade(), kk.getFquantity(), kk.getPacketid(), kk.getFthickness(),
								kk.getPlannedwidth(), kk.getPlannedlength(), kk.getQualitystatus(),
								kk.getPlannedweight(), kk.getPacketstatus(), kk.getEndusertagname() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getMonthwisePlanTrackerDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	public Map<String, Object[]> getEnsUserTagwisePlanTrackerDetails(int partyId, int currentMonth, int currentYear) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<MonthwisePlanTrackerEnduserViewEntity> partyList = endUserwiseViewRepository
					.findByPartyidAndMnthAndYer(partyId, currentMonth, currentYear);

			acctStatementMap.put("1", new Object[] { "Batch No", "Material Type", "Material Grade", "Packet Id",
					"Thickness", "Width", "Length", "Packet Qty", "Packet Status", "End User" });
			int cnt = 1;
			for (MonthwisePlanTrackerEnduserViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCustomerbatchid(), kk.getMaterialdesc(), kk.getMaterialgrade(),
								kk.getPacketid(), kk.getFthickness(), kk.getPlannedwidth(), kk.getPlannedlength(),
								kk.getPlannedweight(), kk.getPacketstatus(), kk.getEndusertagname() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getEnsUserTagwisePlanTrackerDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	@Override
	public boolean createStockDetailsReport(int partyId, String strDate, MimeMessageHelper helper ) {

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
			XSSFSheet spreadsheet = workbook.createSheet("StockDetails_Report");

			// Create row object
			XSSFRow row;

			Map<String, Object[]> acctStatementMap = getStockDetailsReportDetails(partyId);

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
				    if (obj != null) {
						cell.setCellValue("" + obj);
					} else {
						cell.setCellValue("");
					}
				}
			}
			
            String baseDirectory = env.getProperty("email.folderpath")+File.separator;
            //System.out.println("folderpath -- "+baseDirectory);
            
			File outputPojoDirectory = new File(baseDirectory);
			outputPojoDirectory.mkdirs();
			
			File fullPath = new File(baseDirectory +File.separator+"StockDetailsReport_"+strDate+".xlsx");
			
			FileOutputStream out = new FileOutputStream(fullPath);
			workbook.write(out);
			
			FileSystemResource file = new FileSystemResource(fullPath);
			if(acctStatementMap!=null && acctStatementMap.size()>1) {
				attachmentRequired=false;
				helper.addAttachment("StockDetailsReport_" + strDate + ".xlsx", file);
			}
			
			out.close();
			fullPath.deleteOnExit();
			//System.out.println("File Created At -- " + baseDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentRequired;
	}

	public Map<String, Object[]> getStockDetailsReportDetails(int partyId) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<StockDetailsReportViewEntity> partyList = stockDetailsReportViewRepository.findByPartyId(partyId);

			acctStatementMap.put("1",
					new Object[] { "Current Date", "Finishing Date", "EPA Name", "EPA location", "Mtrl. Age(Days)",
							"No'of Pieces", "Classification Tag", "Customer Name", "Material Desc", "Parent Batch (TSL)",
							"EPA Input Batch", "Child Packet Id", "MaterialGrade", "TDC No", "Thickness", "Actual Width",
							"Actual Length", "Quality Remarks (For Deviation)", "Net Wt (Mt)"});

			int cnt = 1;
			for (StockDetailsReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
				new Object[] { kk.getCurrentdate(), kk.getFinishingdate(), kk.getEpaname(), kk.getEpalocation(),
				kk.getCoilage(), kk.getNoofpieces(), kk.getClassificationTag(), kk.getEndusertagname(),
				kk.getMaterialdesc(), kk.getParentbatch(), kk.getEpainputbatch(),
				kk.getPacketId(), kk.getMaterialgrade(), kk.getTdcNo(), kk.getFthickness(), kk.getActualwidth(),
				kk.getActuallength(), kk.getQuality(), kk.getNetweight() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getStockDetailsReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	@Override
	public MonthlySummaryReportEntity getMonthlySummaryReport(List<Integer> partyIdList, int currentMonth,
			int currentYear) {

		MonthlySummaryReportEntity entity = new MonthlySummaryReportEntity();

		try {
			List<Object[]> resultList = monthlySummaryReportRepository.getOpeningStock(partyIdList);

			if (resultList != null && !resultList.isEmpty()) {
				Object[] result = resultList.get(0);

				entity.setCurrentYear(currentYear);
				entity.setCurrentMonth(currentMonth);

				entity.setCurrentStock(result[0] != null ? new BigDecimal(result[0].toString()) : BigDecimal.ZERO);

				entity.setRmCrcoil(result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO);
				entity.setRmCrsheet(result[2] != null ? new BigDecimal(result[2].toString()) : BigDecimal.ZERO);
				entity.setRmHrpocoil(result[3] != null ? new BigDecimal(result[3].toString()) : BigDecimal.ZERO);
				entity.setRmHr(result[4] != null ? new BigDecimal(result[4].toString()) : BigDecimal.ZERO);
				entity.setRmGpcoil(result[5] != null ? new BigDecimal(result[5].toString()) : BigDecimal.ZERO);

				entity.setWipCrcoil(result[6] != null ? new BigDecimal(result[6].toString()) : BigDecimal.ZERO);
				entity.setWipCrsheet(result[7] != null ? new BigDecimal(result[7].toString()) : BigDecimal.ZERO);
				entity.setWipHrpocoil(result[8] != null ? new BigDecimal(result[8].toString()) : BigDecimal.ZERO);
				entity.setWipHr(result[9] != null ? new BigDecimal(result[9].toString()) : BigDecimal.ZERO);
				entity.setWipGpcoil(result[10] != null ? new BigDecimal(result[10].toString()) : BigDecimal.ZERO);

				entity.setFgCrcoil(result[11] != null ? new BigDecimal(result[11].toString()) : BigDecimal.ZERO);
				entity.setFgCrsheet(result[12] != null ? new BigDecimal(result[12].toString()) : BigDecimal.ZERO);
				entity.setFgHrpocoil(result[13] != null ? new BigDecimal(result[13].toString()) : BigDecimal.ZERO);
				entity.setFgHr(result[14] != null ? new BigDecimal(result[14].toString()) : BigDecimal.ZERO);
				entity.setFgGpcoil(result[15] != null ? new BigDecimal(result[15].toString()) : BigDecimal.ZERO);

				entity.setEdgetrimsCrcoil(result[16] != null ? new BigDecimal(result[16].toString()) : BigDecimal.ZERO);
				entity.setEdgetrimsCrsheet(result[17] != null ? new BigDecimal(result[17].toString()) : BigDecimal.ZERO);
				entity.setEdgetrimsHrpocoil(result[18] != null ? new BigDecimal(result[18].toString()) : BigDecimal.ZERO);
				entity.setEdgetrimsHr(result[19] != null ? new BigDecimal(result[19].toString()) : BigDecimal.ZERO);
				entity.setEdgetrimsGpcoil(result[20] != null ? new BigDecimal(result[20].toString()) : BigDecimal.ZERO);

				entity.setCutendsCrcoil(result[21] != null ? new BigDecimal(result[21].toString()) : BigDecimal.ZERO);
				entity.setCutendsCrsheet(result[22] != null ? new BigDecimal(result[22].toString()) : BigDecimal.ZERO);
				entity.setCutendsHrpocoil(result[23] != null ? new BigDecimal(result[23].toString()) : BigDecimal.ZERO);
				entity.setCutendsHr(result[24] != null ? new BigDecimal(result[24].toString()) : BigDecimal.ZERO);
				entity.setCutendsGpcoil(result[25] != null ? new BigDecimal(result[25].toString()) : BigDecimal.ZERO);

				entity.setDefectiveHandlingCrcoil(result[26] != null ? new BigDecimal(result[26].toString()) : BigDecimal.ZERO);
				entity.setDefectiveHandlingCrsheet(result[27] != null ? new BigDecimal(result[27].toString()) : BigDecimal.ZERO);
				entity.setDefectiveHandlingHrpocoil(result[28] != null ? new BigDecimal(result[28].toString()) : BigDecimal.ZERO);
				entity.setDefectiveHandlingHr(result[29] != null ? new BigDecimal(result[29].toString()) : BigDecimal.ZERO);
				entity.setDefectiveHandlingGpcoil(result[30] != null ? new BigDecimal(result[30].toString()) : BigDecimal.ZERO);

				entity.setDefectiveRmCrcoil(result[31] != null ? new BigDecimal(result[31].toString()) : BigDecimal.ZERO);
				entity.setDefectiveRmCrsheet(result[32] != null ? new BigDecimal(result[32].toString()) : BigDecimal.ZERO);
				entity.setDefectiveRmHrpocoil(result[33] != null ? new BigDecimal(result[33].toString()) : BigDecimal.ZERO);
				entity.setDefectiveRmHr(result[34] != null ? new BigDecimal(result[34].toString()) : BigDecimal.ZERO);
				entity.setDefectiveRmGpcoil(result[35] != null ? new BigDecimal(result[35].toString()) : BigDecimal.ZERO);
				entity.setCreatedAt(LocalDateTime.now());
				
				
				List<Object[]> resultCamList = monthlySummaryReportRepository.getOpeningCamStock(partyIdList);

				if (resultCamList != null && !resultCamList.isEmpty()) {
					Object[] resultCam = resultCamList.get(0);

					entity.setRmCamPurushotham(resultCam[0] != null ? new BigDecimal(resultCam[0].toString()) : BigDecimal.ZERO);
					entity.setRmCamUmesh(resultCam[1] != null ? new BigDecimal(resultCam[1].toString()) : BigDecimal.ZERO);
					entity.setRmCamDivakar(resultCam[2] != null ? new BigDecimal(resultCam[2].toString()) : BigDecimal.ZERO);
					entity.setRmCamNiraj(resultCam[3] != null ? new BigDecimal(resultCam[3].toString()) : BigDecimal.ZERO);

					entity.setWipCamPurushotham(resultCam[4] != null ? new BigDecimal(resultCam[4].toString()) : BigDecimal.ZERO);
					entity.setWipCamUmesh(resultCam[5] != null ? new BigDecimal(resultCam[5].toString()) : BigDecimal.ZERO);
					entity.setWipCamDivakar(resultCam[6] != null ? new BigDecimal(resultCam[6].toString()) : BigDecimal.ZERO);
					entity.setWipCamNiraj(resultCam[7] != null ? new BigDecimal(resultCam[7].toString()) : BigDecimal.ZERO);

					entity.setFgCamPurushotham(resultCam[8] != null ? new BigDecimal(resultCam[8].toString()) : BigDecimal.ZERO);
					entity.setFgCamUmesh(resultCam[9] != null ? new BigDecimal(resultCam[9].toString()) : BigDecimal.ZERO);
					entity.setFgCamDivakar(resultCam[10] != null ? new BigDecimal(resultCam[10].toString()) : BigDecimal.ZERO);
					entity.setFgCamNiraj(resultCam[11] != null ? new BigDecimal(resultCam[11].toString()) : BigDecimal.ZERO);

					entity.setEdgetrimsCamPurushotham(resultCam[12] != null ? new BigDecimal(resultCam[12].toString()) : BigDecimal.ZERO);
					entity.setEdgetrimsCamUmesh(resultCam[13] != null ? new BigDecimal(resultCam[13].toString()) : BigDecimal.ZERO);
					entity.setEdgetrimsCamDivakar(resultCam[14] != null ? new BigDecimal(resultCam[14].toString()) : BigDecimal.ZERO);
					entity.setEdgetrimsCamNiraj(resultCam[15] != null ? new BigDecimal(resultCam[15].toString()) : BigDecimal.ZERO);

					entity.setCutendsCamPurushotham(resultCam[16] != null ? new BigDecimal(resultCam[16].toString()) : BigDecimal.ZERO);
					entity.setCutendsCamUmesh(resultCam[17] != null ? new BigDecimal(resultCam[17].toString()) : BigDecimal.ZERO);
					entity.setCutendsCamDivakar(resultCam[18] != null ? new BigDecimal(resultCam[18].toString()) : BigDecimal.ZERO);
					entity.setCutendsCamNiraj(resultCam[19] != null ? new BigDecimal(resultCam[19].toString()) : BigDecimal.ZERO);

					entity.setDefhandlingCamPurushotham(resultCam[20] != null ? new BigDecimal(resultCam[20].toString()) : BigDecimal.ZERO);
					entity.setDefhandlingCamUmesh(resultCam[21] != null ? new BigDecimal(resultCam[21].toString()) : BigDecimal.ZERO);
					entity.setDefhandlingCamDivakar(resultCam[22] != null ? new BigDecimal(resultCam[22].toString()) : BigDecimal.ZERO);
					entity.setDefhandlingCamNiraj(resultCam[23] != null ? new BigDecimal(resultCam[23].toString()) : BigDecimal.ZERO);

					entity.setDefrmCamPurushotham(resultCam[24] != null ? new BigDecimal(resultCam[24].toString()) : BigDecimal.ZERO);
					entity.setDefrmCamUmesh(resultCam[25] != null ? new BigDecimal(resultCam[25].toString()) : BigDecimal.ZERO);
					entity.setDefrmCamDivakar(resultCam[26] != null ? new BigDecimal(resultCam[26].toString()) : BigDecimal.ZERO);
					entity.setDefrmCamNiraj(resultCam[27] != null ? new BigDecimal(resultCam[27].toString()) : BigDecimal.ZERO);
				}
				monthlySummaryReportRepository.save(entity);
			}
		} catch (Exception e) {
			LOGGER.error("Error at getMonthlySummaryReport: ", e);
		}

		return entity;
	}
	
	
	
}
