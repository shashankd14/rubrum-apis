package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterFiledataRepository extends JpaRepository<MaterialMasterFileDataEntity, Integer> {

	@Query("select inw from MaterialMasterFileDataEntity inw where inw.mmId not in (select mm.mmId from MaterialMasterJswEntity mm)")
	List<MaterialMasterFileDataEntity> findAll();

	MaterialMasterFileDataEntity findFirstByMmId(String mmid);

}
