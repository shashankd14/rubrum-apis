package com.steel.product.application.service;

import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.entity.InwardEntry;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;

public interface InwardEntryService {
	
	InwardEntry saveEntry(InwardEntry paramInwardEntry);

	List<InwardEntry> getAllEntries();

	InwardEntry getByEntryId(int inwardId);

	InwardEntry getByCoilNumber(String coilNumber);

	ResponseEntity<Object> getInwardEntriesByPartyId(int paramInt);

	void deleteById(int paramInt);

	void deleteEntity(InwardEntry paramInwardEntry);

	boolean isCoilNumberPresent(String paramString);

	boolean isCustomerBatchIdPresent(String customerBatchId);

	List<InwardEntry> getAllEntriesPwr();

	List<InwardEntry> findDeliveryItemsByInstructionIds(List<Integer> instructionIds);

	List<InwardEntry> saveAll(Set<InwardEntry> inwardEntries);

	InwardEntry getByInwardEntryId(Integer inwardId);

	List<InwardEntryResponseDto> findAllInwards();

	Page<InwardEntry> findAllWithPagination(int pageNo, int pageSize, String searchText);

	List<InwardEntry> findInwardByPartyId(Integer partyId);
}
