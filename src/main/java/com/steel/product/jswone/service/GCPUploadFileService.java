package com.steel.product.jswone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dao.InwardReportViewRepository;
import com.steel.product.application.dto.instruction.WIPChildListResponseDTO;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.inward.WIPListResponseDTO;
import com.steel.product.application.entity.InwardReportViewEntity;
import com.steel.product.application.service.InwardEntryService;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class JsonFileService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	InwardReportViewRepository inwardReportViewRepository;

	@Autowired
	InwardEntryRepository inwardEntryRepository;

	@Autowired
	InwardEntryService inwdEntrySvc;

	@Autowired
	Environment env;

	public void writeJsonToFile(String path, String date) throws IOException {

		List<InwardReportViewEntity> partyList = inwardReportViewRepository.findAll();
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path+"/InwardReports/InwardReport_"+date+".json"), partyList);
		System.out.println("InwardReports Written on "+date);
		
		Map<String, Object> kk = inwardList();
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path+"/InwardDetails/InwardDetails_"+date+".json"), kk);
		System.out.println("InwardDetails Written on "+date);
	}
	
	public Map<String, Object> inwardList() {
		SearchListPageRequest request = new SearchListPageRequest();
		
		request.setPageNo(1);
		request.setPageSize(1000);
		log.info("in wiplist ");
		Map<String, Object> response = new HashMap<>(); 
		
		List<Object[]> packetsList = inwardEntryRepository.wipListNewQuery();

		Map<Integer, WIPListResponseDTO> inwardMap = new LinkedHashMap<>();
		List<WIPChildListResponseDTO> childList = new ArrayList<WIPChildListResponseDTO>();
		for (Object[] result : packetsList) {

			WIPChildListResponseDTO child = new WIPChildListResponseDTO();
			WIPListResponseDTO parent = new WIPListResponseDTO();
			parent.setInwardEntryId(result[1] != null ? (Integer) result[1] : null);
			parent.setCoilNumber(result[2] != null ? (String) result[2] : null);
			parent.setCustomerBatchId(result[3] != null ? (String) result[3] : null);
			parent.setCoilAge(result[4] != null ? (Integer) result[4] : null);
			parent.setPartyName(result[5] != null ? (String) result[5] : null);
			parent.setInwardStatus(result[6] != null ? (String) result[6] : null);
			parent.setMaterialDesc(result[7] != null ? (String) result[7] : null);
			parent.setMaterialGrade(result[8] != null ? (String) result[8] : null);
			parent.setMaterialSubGrade(result[9] != null ? (String) result[9] : null);
			parent.setMmId(result[10] != null ? (String) result[10] : null);
			parent.setFLength(result[11] != null ? (Float) result[11] : null);
			parent.setFThickness(result[12] != null ? (Float) result[12] : null);
			parent.setFWidth(result[13] != null ? (Float) result[13] : null);
			parent.setFpresent(result[14] != null ? (Float) result[14] : null);
			parent.setGrossWeight(result[15] != null ? (Float) result[15] : null);
			child.setInstructionId(result[0] != null ? (Integer) result[0] : null);

			child.setActualLength(result[16] != null ? (Float) result[16] : null);
			child.setActualNoOfPieces(result[17] != null ? (Integer) result[17] : null);
			child.setActualWeight(result[18] != null ? (Float) result[18] : null);
			child.setActualWidth(result[19] != null ? (Float) result[19] : null);
			child.setInstructionDate(result[20] != null ? (Date) result[20] : null);
			child.setPlannedLength(result[22] != null ? (Float) result[22] : null); // 22
			child.setPlannedNoOfPieces(result[23] != null ? (Integer) result[23] : null);
			child.setPlannedWeight(result[24] != null ? (Float) result[24] : null);
			child.setPlannedWidth(result[25] != null ? (Float) result[25] : null); // 25
			child.setAdditionalWeight(result[26] != null ? (Float) result[26] : null);
			child.setClassificationName(result[27] != null ? (String) result[27] : null);
			child.setEndUserTagName(result[28] != null ? (String) result[28] : null);
			child.setStatusName(result[29] != null ? (String) result[29] : null);
			child.setProcessName(result[30] != null ? (String) result[30] : null);
			child.setSoNo(result[31] != null ? (String) result[31] : null);

			if (inwardMap != null && inwardMap.get(parent.getInwardEntryId()) != null) {
				childList = inwardMap.get(parent.getInwardEntryId()).getInstruction();
				childList.add(child);
				parent.setInstruction(childList);
			} else {
				childList = new ArrayList<WIPChildListResponseDTO>();
				childList.add(child);
				parent.setInstruction(childList);
			}
			inwardMap.put(parent.getInwardEntryId(), parent);
		}
		List<WIPListResponseDTO> inwardList = new ArrayList<>(inwardMap.values());
		log.info("In wiplist === " + packetsList); 
		response.put("content", inwardList); 
		return response;
	}
	
}
