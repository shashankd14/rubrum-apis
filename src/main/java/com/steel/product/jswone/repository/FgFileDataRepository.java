package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.FgFileDataEntity;

@Repository
public interface FgFileDataRepository extends JpaRepository<FgFileDataEntity, Integer> {

	/** Same coil + packet uploaded earlier -> the row is updated instead of duplicated. */
	FgFileDataEntity findFirstByCoilNumberAndPacketId(String coilNumber, String packetId);

	FgFileDataEntity findFirstByMmId(String mmId);

	/** Coil already uploaded earlier -> the row is skipped (no insert / update, no material master). */
	boolean existsByCoilNumber(String coilNumber);

	/** A spec already given an MM_ID (9999-...) by an earlier FG upload. */
	@Query(value = "select mm_id from jsw_fg_file_data where mm_description = :mmDescription "
			+ " and mm_id regexp '^9999-[0-9]+-[0-9]+$' limit 1", nativeQuery = true)
	String findMmIdByDescription(@Param("mmDescription") String mmDescription);

	/** Max of each part of MM_IDs shaped 'NNNN-NNNNN-NNNNNNNN': [product code, grade code, running number]. */
	@Query(value = "select max(cast(substring_index(mm_id, '-', 1) as unsigned)), "
			+ " max(cast(substring_index(substring_index(mm_id, '-', 2), '-', -1) as unsigned)), "
			+ " max(cast(substring_index(mm_id, '-', -1) as unsigned)) "
			+ " from jsw_fg_file_data where mm_id regexp '^[0-9]+-[0-9]+-[0-9]+$'", nativeQuery = true)
	List<Object[]> findMaxMmIdParts();

	/** Earlier upload of the same product + grade -> reuse its 9999-<grade code> prefix. */
	@Query(value = "select mm_id from jsw_fg_file_data where upper(trim(material_desc)) = upper(:materialDesc) "
			+ " and form = :form and upper(coalesce(trim(material_grade), '')) = upper(coalesce(:grade, '')) "
			+ " and mm_id regexp '^9999-[0-9]+-[0-9]+$' limit 1", nativeQuery = true)
	String findMmIdByProductAndGrade(@Param("materialDesc") String materialDesc, @Param("form") String form,
			@Param("grade") String grade);
}
