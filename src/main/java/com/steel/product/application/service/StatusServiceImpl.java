package com.steel.product.application.service;

import com.steel.product.application.dao.StatusRepository;
import com.steel.product.application.entity.Status;
import com.steel.product.application.service.StatusService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {
  private StatusRepository statusRepo;
  
  @Autowired
  public StatusServiceImpl(StatusRepository theStatusRepo) {
    this.statusRepo = theStatusRepo;
  }
  
  public Status saveStatus(Status status) {
    return (Status)this.statusRepo.save(status);
  }
  
  public List<Status> getAllStatus() {
    return this.statusRepo.findAll();
  }
  
  public Status getStatusById(int id) {
    Optional<Status> result = this.statusRepo.findById(Integer.valueOf(id));
    Status status = null;
    if (result.isPresent()) {
      status = result.get();
    } else {
      throw new RuntimeException("Did not find status id - " + id);
    } 
    return status;
  }

  @Override
  public List<Status> findStatusByStatusIdIn(List<Integer> statusIds) {
    return statusRepo.getByStatusIdIn(statusIds);
  }
}
