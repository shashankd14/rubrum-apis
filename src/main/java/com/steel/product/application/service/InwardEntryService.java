package com.steel.product.application.service;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
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

  public List<InwardEntry> findDeliveryItemsByInstructionIds(List<Integer> instructionIds);

  public List<InwardEntry> saveAll(Set<InwardEntry> inwardEntries);

  public InwardEntry getByInwardEntryId(Integer inwardId);
}
