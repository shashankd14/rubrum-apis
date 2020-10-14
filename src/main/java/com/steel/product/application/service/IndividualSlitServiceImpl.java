package com.steel.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.IndividualSlitRepository;
import com.steel.product.application.entity.IndividualSlit;

@Service
public class IndividualSlitServiceImpl implements IndividualSlitService {

	@Autowired
	private IndividualSlitRepository individualSlitRepo;
	
	@Override
	public List<IndividualSlit> findAll() {
		
		return individualSlitRepo.findAll();
	}

	@Override
	public IndividualSlit findById(int theId) {
		
		Optional<IndividualSlit> result = individualSlitRepo.findById(theId);
		
		IndividualSlit theSlit = null;
		
		if (result.isPresent()) {
			theSlit = result.get();
		}
		else {
			// we didn't find the employee
			throw new RuntimeException("Did not find slit details id - " + theId);
		}
		
		return theSlit;
	}

	@Override
	public void save(IndividualSlit slitDetails) {
		
		individualSlitRepo.save(slitDetails);

	}

	@Override
	public void deleteById(int theId) {
		
		individualSlitRepo.deleteById(theId);

	}

}
