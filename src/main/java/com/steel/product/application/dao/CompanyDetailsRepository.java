package com.steel.product.application.dao;

import com.steel.product.application.entity.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails,Integer> {
}
