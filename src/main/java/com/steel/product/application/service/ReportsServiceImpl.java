package com.steel.product.application.service;

import com.steel.product.application.dao.FGReportViewRepository;
import com.steel.product.application.dao.InwardReportViewRepository;
import com.steel.product.application.dao.OutwardReportViewRepository;
import com.steel.product.application.dao.RMReportViewRepository;
import com.steel.product.application.dao.StockReportViewRepository;
import com.steel.product.application.dao.StockSummaryReportViewRepository;
import com.steel.product.application.dao.WIPReportViewRepository;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.FGReportViewEntity;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.InwardReportViewEntity;
import com.steel.product.application.entity.OutwardReportViewEntity;
import com.steel.product.application.entity.RMReportViewEntity;
import com.steel.product.application.entity.StockReportViewEntity;
import com.steel.product.application.entity.StockSummaryReportViewEntity;
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
	OutwardReportViewRepository outwardReportViewRepository;

	@Autowired
	InwardReportViewRepository inwardReportViewRepository;

	@Autowired
	StockReportViewRepository stockReportViewRepository;
	
	@Autowired
	FGReportViewRepository fgReportViewRepository;
	
	@Autowired
	WIPReportViewRepository wipReportViewRepository;
	
	@Autowired
	RMReportViewRepository rmReportViewRepository;
	
	@Autowired
	StockSummaryReportViewRepository stockSummaryReportViewRepository;
	
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

			// Create row object
			XSSFRow row;
			
			List<FGReportViewEntity> fgReportDetailsList =getFGReportDetails(partyId);
			
			Map<String, Object[]> fgAcctStatementMap = getFGCassificationDetails(fgReportDetailsList);
			Map<String, Object[]> othersActStatementMap = getOthersCassificationDetails( fgReportDetailsList);

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
					cell.setCellValue((String) obj);
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
					new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","MaterialDesc", "MaterialGrade","Packet Id",
							"Thickness", "Actual Width", "Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				if("FG".equals(kk.getClassificationTag())) {
					cnt++;
					acctStatementMap.put("" + cnt,
					new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getFinishingDate(), kk.getMaterialDesc(),
					kk.getMaterialGrade(),kk.getPacketId(),
					kk.getThickness(), kk.getActualwidth(), kk.getActuallength(), kk.getActualweight(),
					kk.getClassificationTag(), kk.getEnduserTagName() });
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error at getFGReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}

	public Map<String, Object[]> getOthersCassificationDetails(List<FGReportViewEntity> partyList ) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "Finishing Date","MaterialDesc", "MaterialGrade","Packet Id",
							"Thickness", "Actual Width", "Actual Length", "Actual Weight", "Classification Tag", "End User Tag" });

			int cnt = 1;
			for (FGReportViewEntity kk : partyList) {
				if(!("FG".equals(kk.getClassificationTag()))) {
					cnt++;
					acctStatementMap.put("" + cnt,
					new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getFinishingDate(), kk.getMaterialDesc(),
					kk.getMaterialGrade(),kk.getPacketId(),
					kk.getThickness(), kk.getActualwidth(), kk.getActuallength(), kk.getActualweight(),
					kk.getClassificationTag(), kk.getEnduserTagName() });
				}
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
							"Width", "Length", "Net Weight", "In Stock Weight", "WIP Weight", "Packet id","Thickness",
							"Planned Width", "Planned Length", "Planned Weight", "Inward Status", "Classification Tag",
							"End User Tag" });

			int cnt = 1;
			for (WIPReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt, new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(),
						kk.getMaterialDesc(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
						kk.getFlength(), kk.getNetWeight(), kk.getInStockWeight(), kk.getWipWeight(), kk.getPacketId(), 
						kk.getThickness(), kk.getPlannedWidth(), kk.getPlannedLength(), kk.getPlannedWeight(), 
						kk.getInwardStatus(), kk.getClassificationTag(), kk.getEnduserTagName() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getWIPReportDetails " + e.getMessage());
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
					cell.setCellValue((String) obj);
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
							"Dispatched Qty", "InwardStatus" });

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
								kk.getDispatchedweight(), kk.getInwardstatus() });
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
					cell.setCellValue((String) obj);
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
					new Object[] { "CoilNumber", "CustomerBatchId", "Received Date","MaterialDesc", "MaterialGrade", "Thickness",
							"Width", "Length", "Net Weight", "Customer Invoice Number", "Customer Invoice Date", "Status", "Created On" });

			int cnt = 1;
			for (RMReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilNumber(), kk.getCustomerBatchId(), kk.getReceivedDate(),
								kk.getDescription(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
								kk.getFlength(), kk.getNetWeight(), kk.getCustInvNo(), kk.getCustInvDate(),
								kk.getInwardStatus(), kk.getCreatedOn() });
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
			Integer month, Map<Integer, String> months) {

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

			Map<String, Object[]> acctStatementMap = getMonthlyInwardReportDetails(partyId, month);

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
			Integer month, Map<Integer, String> months) {

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

			Map<String, Object[]> acctStatementMap = getMonthlyOutwardReportDetails(partyId, month);

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
					cell.setCellValue((String) obj);
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
	
	public Map<String, Object[]> getMonthlyInwardReportDetails(Integer partyId, Integer month) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<InwardReportViewEntity> partyList = inwardReportViewRepository.findByPartyIdAndMnth(partyId, month);

			acctStatementMap.put("1",
					new Object[] { "CustomerName", "CoilNumber", "CustomerBatchId", "ReceivedDate", "MaterialDesc",
							"MaterialGrade", "Thickness", "Width", "Length", "NetWeight", "customerinvoiceno",
							"customerinvoicedate", "InwardStatus" });

			int cnt = 1;
			for (InwardReportViewEntity kk : partyList) {
				cnt++;

				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCustomerName(), kk.getCoilnumber(), kk.getCustomerbatchid(),
								kk.getReceivedDate(), kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getFthickness(),
								kk.getFwidth(), kk.getFlength(), kk.getNetWeight(), kk.getCustomerinvoiceno(),
								kk.getCustomerinvoicedate(), kk.getInwardStatus() });
			}
		} catch (Exception e) {
			LOGGER.error("Error at getInwardReportDetails " + e.getMessage());
		}
		return acctStatementMap;
	}
	
	public Map<String, Object[]> getMonthlyOutwardReportDetails(Integer partyId, Integer month) {

		Map<String, Object[]> acctStatementMap = new LinkedHashMap<>();

		try {
			List<OutwardReportViewEntity> partyList = outwardReportViewRepository.findByPartyIdAndMnth(partyId, month);

			acctStatementMap.put("1",
					new Object[] { "CoilNumber", "CustomerBatchId", "CustomerName","MaterialDesc",
							"MaterialGrade", "Thickness", "Width", "Length", "Delivery Weight", "DC No", 
							"DC Date", "Vehicle No"});
			int cnt = 1;
			for (OutwardReportViewEntity kk : partyList) {
				cnt++;
				acctStatementMap.put("" + cnt,
						new Object[] { kk.getCoilnumber(), kk.getCustomerbatchid(), kk.getCustomerName(),
								kk.getMaterialdesc(), kk.getMaterialGrade(), kk.getFthickness(), kk.getFwidth(),
								kk.getFlength(), kk.getDeliveryWeight(), kk.getDeliveryid(), kk.getCreatedon(),
								kk.getVehicleno() });
			}
		} catch (Exception e) {
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
	

	
	
	
	
	
	
	
	
}
