package com.steel.product.jswone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dao.InwardReportViewRepository;
import com.steel.product.application.dto.instruction.WIPChildListResponseDTO;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.pdf.GCPUploadDTO;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class GCPUploadFileService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	InwardReportViewRepository inwardReportViewRepository;

	@Autowired
	InwardEntryRepository inwardEntryRepository;

	@Autowired
	Environment env;

	public void writeJsonToFile(String path, String date) throws IOException {
		//List<InwardReportViewEntity> partyList = inwardReportViewRepository.findAll();
		//objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path+"/InwardReports/InwardReport_"+date+".json"), partyList);
		//System.out.println("InwardReports Written on "+date);

		//Map<String, Object> kk = inwardList();
		//objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path+"/InwardDetails/InwardDetails_"+date+".csv"), kk);
		//System.out.println("InwardDetails Written on "+date);
		//String fileName = "/InwardDetails/InwardDetails_"+date+".csv";

		try {
			String path22 = exportInwardListToCsv(path, date);
	        System.out.println("InwardDetails Written on "+path22);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path+"/InwardDetails/InwardDetails_"+date+".csv"), kk);
		//System.out.println("InwardDetails Written on "+date);
	} 

	public String exportInwardListToCsv(String mainFolderPath, String date) throws Exception {

		String coilFolderPath = mainFolderPath+File.separator+"InwardDetails"+File.separator+"coil";
		// Ensure folder exists
		Path path = Paths.get(coilFolderPath);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
		Map<Integer, GCPUploadDTO> inwardMap = inwardList();

		String parentFilePath = coilFolderPath+File.separator+ "coildetails_" + date + ".csv";
		try (PrintWriter writer = new PrintWriter(new FileWriter(parentFilePath))) {
			writer.println("inwardentryid, BatchNumber, createdon, SCInwardId, customercoilid, PartyName, "
					+ "customerinvoiceno, dbilldate, dinvoicedate, dreceiveddate, flength, fquantity, fthickness, "
					+ "fwidth, fpresent, grossweight, in_stock_weight, isdeleted, parentcoilnumber, purposetype, "
					+ "remarks, testcertificatefileurl, testcertificatenumber, "
					+ "updatedon, tdc_no, vinvoiceno, vlorryno, valueofgoods,  "
					+ "mmid, npartyid, vstatus, uts, el, ys, pdf_s3_url");
			for (Map.Entry<Integer, GCPUploadDTO> entry : inwardMap.entrySet()) {
				GCPUploadDTO gcpUploadDTO = entry.getValue();

				writer.println(gcpUploadDTO.getInwardEntryId() + "," + gcpUploadDTO.getCoilNumber() + ","
						+ gcpUploadDTO.getCreatedOn() + "," + gcpUploadDTO.getCustomerBatchId() + ","
						+ gcpUploadDTO.getCustomerCoilId() + "," + gcpUploadDTO.getPartyName() + ","
						+ gcpUploadDTO.getCustomerInvoiceNo() + "," + gcpUploadDTO.getDBillDate() + ","
						+ gcpUploadDTO.getDInvoiceDate() + "," + gcpUploadDTO.getDReceivedDate() + ","
						+ gcpUploadDTO.getFLength() + "," + gcpUploadDTO.getFQuantity() + ","
						+ gcpUploadDTO.getFThickness() + "," + gcpUploadDTO.getFWidth() + ","
						+ gcpUploadDTO.getFpresent() + "," + gcpUploadDTO.getGrossWeight() + ","
						+ gcpUploadDTO.getInStockWeight() + "," + gcpUploadDTO.getIsDeleted() + ","
						+ gcpUploadDTO.getParentCoilNumber() + "," + gcpUploadDTO.getPurposeType() + ","
						+ gcpUploadDTO.getRemarks() + "," + gcpUploadDTO.getTestCertificateFileUrl() + ","
						+ gcpUploadDTO.getTestCertificateNumber() + "," + gcpUploadDTO.getUpdatedOn() + ","
						+ gcpUploadDTO.getTdcNo() + "," + gcpUploadDTO.getVInvoiceNo() + ","
						+ gcpUploadDTO.getVLorryNo() + "," + gcpUploadDTO.getValueOfGoods() + ","
						+ gcpUploadDTO.getMmid() + "," + "" + "," + gcpUploadDTO.getStatusDesc() + ","
						+ gcpUploadDTO.getUts() + "," + gcpUploadDTO.getEl() + "," + gcpUploadDTO.getYs());
			}
		}
		String skuFolderPath = mainFolderPath+File.separator+"InwardDetails"+File.separator+"sku";
		// Ensure folder exists
		Path skupath = Paths.get(skuFolderPath);
		if (!Files.exists(skupath)) {
			Files.createDirectories(skupath);
		}
		
		String childFilePath = skuFolderPath+File.separator+ "InwardDetails_Packets_" + date + ".csv";
		try (PrintWriter writer = new PrintWriter(new FileWriter(childFilePath))) {
			writer.println("inwardentryid, PacketId, instruction_Date , Planned_Length, Planned_Width, "
					+ " Planned_Weight, Planned_Noof_Pieces, ActualLength, ActualWidth, ActualWeight, "
					+ "Actual_Noof_Pieces, Additional_Weight, Process_Name, Packet_Status, Classification_Name,"
					+ "Remarks, SoNumber");

			for (Map.Entry<Integer, GCPUploadDTO> entry : inwardMap.entrySet()) {
				GCPUploadDTO gcpUploadDTO = entry.getValue();

				List<WIPChildListResponseDTO> packetsList = gcpUploadDTO.getInstructions();
				for (WIPChildListResponseDTO childObj : packetsList) {
					writer.println(childObj.getInwardEntryId() + "," + childObj.getInstructionId() + ","
							+ childObj.getInstructionDate() + "," + childObj.getPlannedLength() + ","
							+ childObj.getPlannedWidth() + "," + childObj.getPlannedWeight() + ","
							+ childObj.getPlannedNoOfPieces() + "," + childObj.getActualLength() + ","
							+ childObj.getActualWidth() + "," + childObj.getActualWeight() + ","
							+ childObj.getActualNoOfPieces() + "," + childObj.getAdditionalWeight() + ","
							+ childObj.getProcessName() + "," + childObj.getStatusName() + ","
							+ childObj.getClassificationName() + "," + childObj.getRemarks() + ","
							+ childObj.getSoNo());
				}

			}
		}
        return childFilePath; // return saved path
	}

	public Map<Integer, GCPUploadDTO> inwardList() {
		SearchListPageRequest request = new SearchListPageRequest();
		
		request.setPageNo(1);
		request.setPageSize(1000);
		log.info("in wiplist ");
		Map<String, Object> response = new HashMap<>(); 
		
		List<Object[]> packetsList = inwardEntryRepository.inwardListDataforGCP();
		Map<Integer, GCPUploadDTO> inwardMap = new LinkedHashMap<>();
		List<WIPChildListResponseDTO> childList = new ArrayList<WIPChildListResponseDTO>();
		for (Object[] result : packetsList) {
			WIPChildListResponseDTO child = new WIPChildListResponseDTO();
			GCPUploadDTO parent = new GCPUploadDTO();
			parent.setMmid( result[0] != null ? (String ) result[0] : null);
			parent.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			child.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			parent.setCoilNumber(result[2] != null ? (String) result[2] : null);
			parent.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
			parent.setCreatedOn( result[4] != null ? (Date) result[4] : null);
			parent.setCustomerInvoiceNo( result[5] != null ? (String) result[5] : null);
			parent.setDBillDate( result[6] != null ? (Date) result[6] : null);
			parent.setDInvoiceDate( result[7] != null ? (Date) result[7] : null);
			parent.setDReceivedDate( result[8] != null ? (Date) result[8] : null);
			parent.setFLength(result[9] != null ? (Float) result[9] : null);
			parent.setFQuantity( result[10] != null ? (Float) result[10] : null);
			parent.setFThickness( result[11] != null ? (Float) result[11] : null);
			parent.setFWidth( result[12] != null ? (Float) result[12] : null);
			parent.setFpresent( result[13] != null ? (Float) result[13] : null);
			parent.setGrossWeight( result[14] != null ? (Float) result[14] : null);
			parent.setInStockWeight( result[15] != null ? (Float) result[15] : null);
			parent.setIsDeleted( result[16] != null ? (Boolean) result[16] : null);
			parent.setRemarks( result[17] != null ? (String) result[17] : null);
			parent.setTestCertificateFileUrl( result[18] != null ? (String) result[18] : null);
			parent.setTestCertificateNumber( result[19] != null ? (String) result[19] : null);
			parent.setUpdatedOn( result[20] != null ? (Date) result[20] : null);
			parent.setTdcNo(result[21] != null ? (String) result[21] : null);
			parent.setVInvoiceNo( result[22] != null ? (String) result[22] : null);
			parent.setVLorryNo( result[23] != null ? (String) result[23] : null);
			parent.setValueOfGoods(result[24] != null ? (Float) result[24] : null);
			parent.setStatusDesc( result[25] != null ? (String) result[25] : null);
			parent.setUts(result[26] != null ? (Float) result[26] : null);
			parent.setEl(result[27] != null ? (Float) result[27] : null);
			parent.setYs(result[28] != null ? (Float) result[28] : null);
			parent.setPartyName(result[29] != null ? (String) result[29] : null);
			parent.setCoilage( result[30] != null ? (Integer) result[30] : null);

			child.setInstructionId( result[34] != null ? (Integer) result[34] : null);
			child.setActualLength(result[35] != null ? (Float) result[35] : null);
			child.setActualNoOfPieces(result[36] != null ? (Integer) result[36] : null);
			child.setActualWeight(result[37] != null ? (Float) result[37] : null);
			child.setActualWidth(result[38] != null ? (Float) result[38] : null);
			child.setInstructionDate(result[39] != null ? (Date) result[39] : null);
			child.setPlannedLength(result[40] != null ? (Float) result[40] : null); // 22
			child.setPlannedNoOfPieces(result[41] != null ? (Integer) result[41] : null);
			child.setPlannedWeight(result[42] != null ? (Float) result[42] : null);
			child.setPlannedWidth(result[43] != null ? (Float) result[43] : null); // 25
			child.setAdditionalWeight(result[44] != null ? (Float) result[44] : null);
			child.setClassificationName(result[45] != null ? (String) result[45] : null);
			child.setEndUserTagName(result[46] != null ? (String) result[46] : null);
			child.setStatusName(result[47] != null ? (String) result[47] : null);
			child.setProcessName(result[48] != null ? (String) result[48] : null);
			child.setSoNo(result[49] != null ? (String) result[49] : null);

			if (inwardMap != null && inwardMap.get(parent.getInwardEntryId()) != null) {
				childList = inwardMap.get(parent.getInwardEntryId()).getInstructions();
				childList.add(child);
				parent.setInstructions(childList);
			} else {
				childList = new ArrayList<WIPChildListResponseDTO>();
				childList.add(child);
				parent.setInstructions(childList);
			}
			inwardMap.put( parent.getInwardEntryId(), parent);
		}
		log.info("inwardMap cnt == " + inwardMap.size()); 
		return inwardMap;
	}
	
	
}
