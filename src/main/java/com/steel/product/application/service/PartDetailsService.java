package com.steel.product.application.service;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.entity.PartDetails;
import java.util.List;
import java.util.Set;

public interface PartDetailsService {

    List<PartDetails> saveAll(List<PartDetails> partDetails);

    List<PartDetails> saveAll(Set<PartDetails> partDetails);

    PartDetails save(PartDetails partDetails);

    List<PartDetailsResponse> getByPartDetailsId(String instructionPlanId);

    List<PartDetails> findAllByPartDetailsId(String partDetailsId);

    PartDetails findById(Long partId);


}
