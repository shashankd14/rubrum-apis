package com.steel.product.application.service;

import java.util.List;

import com.steel.product.application.entity.IndividualCut;

public interface IndividualCutService {
	
	public List<IndividualCut> findAll();
	
	public IndividualCut findById(int theId);
	
	public void save(IndividualCut cutDetails);
	
	public void deleteById(int theId);

}
