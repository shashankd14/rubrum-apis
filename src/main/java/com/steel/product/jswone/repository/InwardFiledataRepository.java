package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.InwardFileDataEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardFiledataRepository extends JpaRepository<InwardFileDataEntity, Integer> {

	@Query("select inw from InwardFileDataEntity inw where inw.batchnumber not in (select mm.batchNumber from InwardEntry mm)")
	List<InwardFileDataEntity> findAll();
}
