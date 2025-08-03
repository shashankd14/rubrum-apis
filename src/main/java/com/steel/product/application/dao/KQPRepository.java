package com.steel.product.application.dao;

import com.steel.product.application.dto.quality.QualityTemplateResponse;
import com.steel.product.application.entity.KQPEntity;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KQPRepository extends JpaRepository<KQPEntity, Integer> {

	KQPEntity findFirstByKqpName(String kqpName);
	
	KQPEntity findByKqpId(int kqpId);

	List<KQPEntity> findAllByOrderByKqpIdDesc();
}
