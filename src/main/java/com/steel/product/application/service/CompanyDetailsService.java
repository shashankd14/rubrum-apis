package com.steel.product.application.service;

import com.steel.product.application.dao.CompanyDetailsRepository;
import com.steel.product.application.entity.CompanyDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyDetailsService {

    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    public CompanyDetailsService(CompanyDetailsRepository companyDetailsRepository) {
        this.companyDetailsRepository = companyDetailsRepository;
    }

    public CompanyDetails findById(Integer id){
        Optional<CompanyDetails> companyDetailsOptional = companyDetailsRepository.findById(id);
        if(!companyDetailsOptional.isPresent()){
            return null;
        }
        return companyDetailsOptional.get();
    }
}
