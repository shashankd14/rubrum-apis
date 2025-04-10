package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.InwardFileDataEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardFiledataRepository extends JpaRepository<InwardFileDataEntity, Integer> {

	@Query("select inw from InwardFileDataEntity inw where inw.coilno not in (select mm.coilNumber from InwardEntry mm)")
	List<InwardFileDataEntity> findAll();
}
