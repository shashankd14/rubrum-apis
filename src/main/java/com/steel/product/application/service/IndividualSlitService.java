package com.steel.product.application.service;

import java.util.List;

import com.steel.product.application.entity.IndividualSlit;

public interface IndividualSlitService {
	
	public List<IndividualSlit> findAll();
	
	public IndividualSlit findById(int theId);
	
	public void save(IndividualSlit slitDetails);
	
	public void deleteById(int theId);

}
