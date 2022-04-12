package com.steel.product.application.service;

import com.steel.product.application.dao.InwardEntryRepository;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.entity.InwardEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InwardEntryServiceImpl implements InwardEntryService {

	private final static Logger LOGGER = LoggerFactory.getLogger("InwardEntryServiceImpl");
	private final InwardEntryRepository inwdEntryRepo;

	@Autowired
	public InwardEntryServiceImpl(InwardEntryRepository theInwdEntryRepo) {
		this.inwdEntryRepo = theInwdEntryRepo;
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
		return new ResponseEntity(entities.stream().map(inw -> InwardEntry.valueOfResponse(inw))
				.collect(Collectors.toList()), HttpStatus.OK);
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
	public Page<InwardEntry> findAllWithPagination(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<InwardEntry> pageResult = inwdEntryRepo.findAll(pageable);
		return pageResult;
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

}
