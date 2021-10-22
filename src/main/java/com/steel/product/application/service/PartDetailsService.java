package com.steel.product.application.service;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.entity.PartDetails;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PartDetailsService {

    public List<PartDetails> saveAll(List<PartDetails> partDetails);

    public List<PartDetails> saveAll(Set<PartDetails> partDetails);

    public PartDetails save(PartDetails partDetails);

    public List<PartDetailsResponse> getByPartDetailsId(String instructionPlanId);

    public List<PartDetails> findAllByPartDetailsId(String partDetailsId);
}
