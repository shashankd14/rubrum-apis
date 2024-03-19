package com.steel.product.application.service;

import com.steel.product.application.dto.delivery.DeliveryPDFRequestDTO;
import com.steel.product.application.dto.inward.InwardEntryResponseDto;
import com.steel.product.application.dto.inward.SearchListPageRequest;
import com.steel.product.application.dto.qrcode.QRCodeResponse;
import com.steel.product.application.entity.InwardEntry;
import net.minidev.json.JSONObject;

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

	Page<InwardEntry> inwardList( SearchListPageRequest searchListPageRequest);

	List<InwardEntry> findInwardByPartyId(Integer partyId);

	JSONObject getPlanPDFs(int inwardEntryId);
	
	Page<InwardEntry> findAllWIPlistWithPagination(int pageNo, int pageSize, String searchText, String partyId);

	QRCodeResponse getInwardQRCodeDetails(int inwardEntryId);

	void updateQRCodeS3InwardPDF(String inwardId, String url);

	void updateQRCodeEditFinish(String inwardId, String url);

	JSONObject getdcpdf(DeliveryPDFRequestDTO req);

	public void updateS3InwardLabelPDF(Integer inwardId, String url);

	JSONObject getLabels(int inwardEntryId, String processType);

	public Page<InwardEntry> partywiselist( SearchListPageRequest searchListPageRequest);

}
