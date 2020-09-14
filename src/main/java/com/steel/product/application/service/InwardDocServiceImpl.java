package com.steel.product.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.InwardDocRepository;
import com.steel.product.application.entity.InwardDoc;

@Service
public class InwardDocServiceImpl implements InwardDocService {

	@Autowired
	private InwardDocRepository inwardDocRepo;
	
	@Override
	public InwardDoc save(InwardDoc inwardDoc) {
		return inwardDocRepo.save(inwardDoc);
	}

}
