package com.steel.product.jswone.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.repository.BranchMasterRepository;
import com.steel.product.jswone.request.SearchRequest;
import com.steel.product.jswone.response.BranchMasterDTO;
import com.steel.product.jswone.response.BranchMasterListRespose;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BranchMasterServiceImpl implements BranchMasterService {

	@Autowired
	BranchMasterRepository branchRepository;

	@Override
	public List<BranchMasterDTO> getList(SearchRequest searchPageRequest) {
		ModelMapper modelMapper = new ModelMapper();
		List<Object[]> branchList = branchRepository.findAllBranchDetails();
		Map<Integer, BranchMasterDTO> soMap = new LinkedHashMap<>();
		
		for (Object[] result : branchList) {
			BranchMasterDTO dto=new BranchMasterDTO();
			BranchMasterListRespose childdto=new BranchMasterListRespose();
			
			dto.setBranchId( result[0] != null ? Integer.parseInt(result[0].toString()) : null);
			dto.setBranchName( result[1] != null ? (String) result[1] : null);
			childdto.setLocationId( result[2] != null ? Integer.parseInt(result[2].toString()) : null);
			childdto.setLocationName(result[3] != null ? (String) result[3] : null);
			dto.getLocationList().add(childdto);
			if (soMap != null && soMap.get(dto.getBranchId() ) != null) {
				BranchMasterDTO addEntity = soMap.get(dto.getBranchId() );
				addEntity.getLocationList().add(childdto);
				soMap.put(dto.getBranchId(), addEntity);
			} else {
				soMap.put(dto.getBranchId(), dto);
			}
		}		
		List<BranchMasterDTO > list = new ArrayList<>(soMap.values());

		List<BranchMasterDTO> dtoList = list.stream().map(entity -> modelMapper.map(entity, BranchMasterDTO.class))
				.collect(Collectors.toList());
		return dtoList;
	}

}
