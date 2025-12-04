package com.steel.product.jswone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.entity.BranchMasterEntity;
import com.steel.product.jswone.repository.BranchMasterRepository;
import com.steel.product.jswone.request.SearchRequest;
import com.steel.product.jswone.response.BranchMasterDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BranchMasterServiceImpl implements BranchMasterService {

	@Autowired
	BranchMasterRepository branchRepository;

	@Override
	public List<BranchMasterDTO> getList(SearchRequest searchPageRequest) {
		ModelMapper modelMapper = new ModelMapper();
		List<BranchMasterEntity> entities = branchRepository.findAll();

		List<BranchMasterDTO> dtoList = entities.stream().map(entity -> modelMapper.map(entity, BranchMasterDTO.class))
				.collect(Collectors.toList());
		return dtoList;
	}

}
