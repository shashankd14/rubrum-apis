package com.steel.product.application.service;

import com.steel.product.application.entity.Status;
import java.util.List;

public interface StatusService {
  Status saveStatus(Status paramStatus);
  
  List<Status> getAllStatus();
  
  Status getStatusById(int paramInt);

  List<Status> findStatusByStatusIdIn(List<Integer> statusIds);
}
