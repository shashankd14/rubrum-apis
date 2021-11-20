package com.steel.product.application.service;

import com.steel.product.application.dao.PartDetailsRepository;
import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.entity.PartDetails;
import com.steel.product.application.mapper.PartDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PartDetailsServiceImpl implements PartDetailsService {

    private PartDetailsRepository partDetailsRepository;
    private PartDetailsMapper partDetailsMapper;

    @Autowired
    public PartDetailsServiceImpl(PartDetailsRepository partDetailsRepository, PartDetailsMapper partDetailsMapper) {
        this.partDetailsRepository = partDetailsRepository;
        this.partDetailsMapper = partDetailsMapper;
    }


    @Override
    public List<PartDetails> saveAll(List<PartDetails> partDetails) {
        return partDetailsRepository.saveAll(partDetails);
    }

    @Override
    public List<PartDetails> saveAll(Set<PartDetails> partDetails) {
        return partDetailsRepository.saveAll(partDetails);
    }

    @Override
    public PartDetails save(PartDetails partDetails) {
        return partDetailsRepository.save(partDetails);
    }

    @Override
    public List<PartDetailsResponse> getByPartDetailsId(String partDetailsId) {
        List<PartDetails> partDetailsList = partDetailsRepository.getByPartDetailsId(partDetailsId);
        if (partDetailsList == null) {
            throw new RuntimeException("part details with partDetailsId " + partDetailsId + " not found");
        }
        return partDetailsMapper.toResponseDto(partDetailsList);
    }

    @Override
    public List<PartDetails> findAllByPartDetailsId(String partDetailsId) {
        return null;
    }

    @Override
    public PartDetails findById(Long partId) {
        Optional<PartDetails> partDetailsOptional = partDetailsRepository.findById(partId);
        if(!partDetailsOptional.isPresent()){
            throw new RuntimeException("Part details with id "+partId+" not found");
        }
        return partDetailsOptional.get();
    }
}
