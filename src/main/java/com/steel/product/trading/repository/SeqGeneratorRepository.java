package com.steel.product.trading.repository;

import com.steel.product.trading.entity.SeqGeneratorEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SeqGeneratorRepository extends JpaRepository<SeqGeneratorEntity, Integer> {

	public List<SeqGeneratorEntity> findAll();
	
	public SeqGeneratorEntity findByFieldName(String fieldName);

	@Modifying
	@Transactional
	@Query(value = "update trading_seq_generator set cur_seq=cur_seq+1 where field_name= :fieldName ", nativeQuery = true)
	public int updateSKUCodeSeq(@Param(value = "fieldName") String fieldName);
}
