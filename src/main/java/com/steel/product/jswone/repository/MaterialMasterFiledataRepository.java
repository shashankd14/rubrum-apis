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

	/** Max of each part of MM_IDs shaped 'NNNN-NNNNN-NNNNNNNN': [product code, grade code, running number]. */
	@Query(value = "select max(cast(substring_index(mm_id, '-', 1) as unsigned)), "
			+ " max(cast(substring_index(substring_index(mm_id, '-', 2), '-', -1) as unsigned)), "
			+ " max(cast(substring_index(mm_id, '-', -1) as unsigned)) "
			+ " from jsw_material_file_data where mm_id regexp '^[0-9]+-[0-9]+-[0-9]+$'", nativeQuery = true)
	List<Object[]> findMaxMmIdParts();

}
