package com.steel.product.application.service;

import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.partDetails.PartDetailsPDFResponse;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.util.CommonUtil;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InwardEntryServiceImpl implements InwardEntryService {

	private final static Logger LOGGER = LoggerFactory.getLogger("InwardEntryServiceImpl");
	private final InwardEntryRepository inwdEntryRepo;
	private AWSS3Service awsS3Service;
	private CommonUtil commonUtil;

	@Autowired
	public InwardEntryServiceImpl(InwardEntryRepository theInwdEntryRepo, AWSS3Service awsS3Service, CommonUtil commonUtil) {
		this.inwdEntryRepo = theInwdEntryRepo;
		this.awsS3Service = awsS3Service;
		this.commonUtil = commonUtil;
	}

	public InwardEntry saveEntry(InwardEntry entry) {
		return (InwardEntry) this.inwdEntryRepo.save(entry);
	}

	public List<InwardEntry> getAllEntries() {
		return this.inwdEntryRepo.findAll();
	}

	public InwardEntry getByEntryId(int id) {
		Optional<InwardEntry> result = this.inwdEntryRepo.findById(Integer.valueOf(id));
		InwardEntry theEntry = null;
		if (result.isPresent()) {
			theEntry = result.get();
		} else {
			throw new RuntimeException("Did not find inward id - " + id);
		}
		return theEntry;
	}

	public ResponseEntity<Object> getInwardEntriesByPartyId(int partyId) {
		List<InwardEntry> entities = new ArrayList<>();
		entities = this.inwdEntryRepo.getInwardEntriesByPartyId(Integer.valueOf(partyId));
		return new ResponseEntity(entities.stream().map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList()), HttpStatus.OK);
	}

	public void deleteById(int id) {
		this.inwdEntryRepo.deleteById(Integer.valueOf(id));
	}

	public void deleteEntity(InwardEntry entry) {
		this.inwdEntryRepo.delete(entry);
	}

	public boolean isCoilNumberPresent(String coilNumber) {
		boolean isPresent = false;
		String value = this.inwdEntryRepo.isCoilNumberPresent(coilNumber);
		if (value != null && value.length() != 0) {
			isPresent = true;
		}
		return isPresent;
	}

	@Override
	public boolean isCustomerBatchIdPresent(String customerBatchId) {
		boolean isPresent = false;
		String value = this.inwdEntryRepo.isCustomerBatchIdPresent(customerBatchId);
		if (value != null && value.length() != 0) {
			isPresent = true;
		}
		return isPresent;
	}

	@Override
	public List<InwardEntry> saveAll(Set<InwardEntry> inwardEntries) {
		return inwdEntryRepo.saveAll(inwardEntries);
	}

	@Override
	public InwardEntry getByInwardEntryId(Integer inwardId) {
		InwardEntry inwardEntry;
		try{
			inwardEntry = inwdEntryRepo.getOne(inwardId);
		}catch (Exception ex){
			throw new RuntimeException("inward with id "+inwardId+" not found");
		}
		return inwardEntry;
	}

	@Override
	public Page<InwardEntry> findAllWithPagination(int pageNo, int pageSize, String searchText, String partyId) {
		LOGGER.info("In findAllWithPagination page ");
		Pageable pageable = PageRequest.of((pageNo-1), pageSize);
		
		if(partyId!=null && partyId.length()>0) {
			Page<InwardEntry> pageResult = inwdEntryRepo.findAll(searchText, Integer.parseInt(partyId), pageable);
			return pageResult;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if(adminUserEntity.getUserPartyMap()!=null && adminUserEntity.getUserPartyMap().size()>0) {
				List<Integer> partyIds=new ArrayList<>();
				for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					partyIds.add(userPartyMap.getPartyId());
					LOGGER.info("In partyIds === "+partyIds);
				}
				Page<InwardEntry> pageResult = inwdEntryRepo.findAll(searchText, partyIds, pageable);
				return pageResult;
			} else {
				Page<InwardEntry> pageResult = inwdEntryRepo.findAll(searchText, pageable);
				return pageResult;
			}
		}
	}

	@Override
	public Page<InwardEntry> findAllWIPlistWithPagination(int pageNo, int pageSize, String searchText, String partyId) {
		LOGGER.info("In findAllWithPagination page ");
		Pageable pageable = PageRequest.of((pageNo-1), pageSize);
		
		if(partyId!=null && partyId.length()>0) {
			Page<InwardEntry> pageResult = inwdEntryRepo.findAllWIP(searchText, Integer.parseInt(partyId), pageable);
			return pageResult;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if(adminUserEntity.getUserPartyMap()!=null && adminUserEntity.getUserPartyMap().size()>0) {
				List<Integer> partyIds=new ArrayList<>();
				for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					partyIds.add(userPartyMap.getPartyId());
					LOGGER.info("In partyIds === "+partyIds);
				}
				Page<InwardEntry> pageResult = inwdEntryRepo.findAllWIP(searchText, partyIds, pageable);
				return pageResult;
			} else {
				Page<InwardEntry> pageResult = inwdEntryRepo.findAllWIP(searchText, pageable);
				return pageResult;
			}
		}
	}
	
	@Override
	public List<InwardEntryResponseDto> findAllInwards() {
		return inwdEntryRepo.findAll().stream()
				.map(inw -> InwardEntry.valueOfResponse(inw)).collect(Collectors.toList());
	}

	@Override
	public List<InwardEntry> findInwardByPartyId(Integer partyId) {
		return inwdEntryRepo.findInwardByPartyId(partyId);
	}

	@Override
	public List<InwardEntry> getAllEntriesPwr() {
		return inwdEntryRepo.findAll().stream()
				.filter(inwardEntry -> inwardEntry.getStatus().getStatusId() != 4 && inwardEntry.getStatus().getStatusId() != 5)
				.peek(inwardEntry -> inwardEntry.getInstructions().stream().filter(ins -> ins.getStatus().getStatusId() != 4 &&
						ins.getStatus().getStatusId() != 5)).collect(Collectors.toList());
	}

	@Override
	public List<InwardEntry> findDeliveryItemsByInstructionIds(List<Integer> instructionIds) {
		return inwdEntryRepo.findDeliveryItemsByInstructionIds(instructionIds);
	}

	@Override
	public InwardEntry getByCoilNumber(String coilNumber) {
		
		Optional<InwardEntry> result = this.inwdEntryRepo.findByCoilNumber(coilNumber);
		InwardEntry theEntry = null;
		if (result.isPresent()) {
			theEntry = result.get();
		} else {
			throw new RuntimeException("Did not find entry with coilNumber - " + coilNumber);
		}
		return theEntry;
	}

	public JSONObject getPlanPDFs(int inwardId) {
		JSONObject finalResp =new JSONObject();
		List<PartDetailsPDFResponse> response = new ArrayList<PartDetailsPDFResponse>();
		List<Object[]> results = this.inwdEntryRepo.getPlanPDFs(inwardId);
		Iterator itr = results.iterator();
		while (itr.hasNext()) {
			PartDetailsPDFResponse kk = new PartDetailsPDFResponse();
			Object result[] = (Object[]) itr.next();
			kk.setId(result[0] != null ? (String) result[0] : null);
			kk.setFileName(result[0] != null ? (String) result[0] : null);
			kk.setPdfS3Url(result[1] != null ? (String) result[1] : null);
			kk.setPdfS3Url(awsS3Service.generatePresignedUrl(kk.getFileName()));
			response.add(kk);
		}
		finalResp.put("plan_pdfs", response);
		
		String s3URL = this.inwdEntryRepo.getS3URL(inwardId);
		finalResp.put("inward_pdf", awsS3Service.generatePresignedUrl(s3URL) );
		
		List<PartDetailsPDFResponse> response2 = new ArrayList<PartDetailsPDFResponse>();
		List<Object[]> result2 = this.inwdEntryRepo.getDCPDFs(inwardId);

		for (Object[] obj2 : result2) {

			PartDetailsPDFResponse kk = new PartDetailsPDFResponse();
			DeliveryDetails partDetails = (DeliveryDetails) obj2[0];
			kk.setId(partDetails.getDeliveryId().toString());
			kk.setFileName(partDetails.getPdfS3Url());
			kk.setPdfS3Url(awsS3Service.generatePresignedUrl(partDetails.getPdfS3Url()));
			response2.add(kk);
		}
		finalResp.put("dc_pdfs", response2);
		
		return finalResp;
	}

}
