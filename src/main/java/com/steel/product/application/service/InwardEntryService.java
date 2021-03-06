package com.steel.product.application.service;

import com.steel.product.application.entity.InwardEntry;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
}
