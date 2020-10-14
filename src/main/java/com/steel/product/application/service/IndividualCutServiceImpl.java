package com.steel.product.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.IndividualCutRepository;
import com.steel.product.application.entity.IndividualCut;

@Service
public class IndividualCutServiceImpl implements IndividualCutService {

	@Autowired
	private IndividualCutRepository individualCutRepo;
	
	@Override
	public List<IndividualCut> findAll() {
		
		return individualCutRepo.findAll();
	}

	@Override
	public IndividualCut findById(int theId) {
		
		Optional<IndividualCut> result = individualCutRepo.findById(theId);
		
		IndividualCut theCut = null;
		
		if (result.isPresent()) {
			theCut = result.get();
		}
		else {
			// we didn't find the employee
			throw new RuntimeException("Did not find cut details id - " + theId);
		}
		
		return theCut;
	}

	@Override
	public void save(IndividualCut cutDetails) {

		individualCutRepo.save(cutDetails);
	}

	@Override
	public void deleteById(int theId) {

		individualCutRepo.deleteById(theId);
	}

}
