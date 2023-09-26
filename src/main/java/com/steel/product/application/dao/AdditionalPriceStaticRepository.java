package com.steel.product.application.dao;

import com.steel.product.application.entity.AdditionalPriceStaticEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalPriceStaticRepository extends JpaRepository<AdditionalPriceStaticEntity, Integer> {
	
	List<AdditionalPriceStaticEntity> findByProcessId(Integer processId);

}
