package com.steel.product.application.dao;

import com.steel.product.application.entity.PartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import javax.transaction.Transactional;

@Repository
public interface PartDetailsRepository extends JpaRepository<PartDetails, Long> {

	List<PartDetails> getByPartDetailsId(String partDetailsId);

	@Query("select pd from PartDetails pd left join fetch pd.instructions ins left join fetch ins.inwardId where pd.partDetailsId = :partDetailsId")
	public List<PartDetails> findAllByPartDetailsId(@Param("partDetailsId") String partDetailsId);

	@Modifying
	@Transactional
	@Query("update PartDetails set qrcodeS3Url=:url where partDetailsId = :partDetailsId ")
	public void updatePartDetailsS3PDF(@Param("partDetailsId") String partDetailsId, @Param("url") String url);

	@Query(value = "select coilnumber, customerbatchid, partyname, instructiondate, material_desc, "
			+ "	material_grade, packet_id, fthickness, plannedwidth, plannedlength, plannedweight, actualwidth,"
			+ "	actuallength, actualweight, enduser_tag_name, plannednoofpieces, parentcoilnumber, is_slit_and_cut, "
			+ "	processid, batchnumber, finishdate, "
			+ "	(select company_name from product_company_details where id=1) compname , "
			+ "	(select email from product_company_details where id=1) email "
			+ " FROM label_print_vw where stts=:stts and part_details_id=:planId ", nativeQuery = true)
	List<Object[]> wipLabelData(@Param("stts") Integer stts, @Param("planId") String planId);

	@Query(value = "select coilnumber, customerbatchid, partyname, instructiondate, material_desc, "
			+ "	material_grade, packet_id, fthickness, plannedwidth, plannedlength, plannedweight, actualwidth,"
			+ "	actuallength, actualweight, enduser_tag_name, actualnoofpieces, parentcoilnumber, is_slit_and_cut, "
			+ "	processid, batchnumber,finishdate, "
			+ "	(select company_name from product_company_details where id=1) compname, "
			+ "	(select email from product_company_details where id=1) email "
			+ " FROM label_print_vw where CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END and stts=:stts and part_details_id=:planId ", nativeQuery = true)
	List<Object[]> fgLabelData(@Param("stts") Integer stts, @Param("planId") String planId);

	@Modifying
	@Transactional
	@Query("update PartDetails pd set pd.actualYieldLossRatio= :actualYieldLossRatio where pd.id =:partDetailsId ")
	public void updateActualYLR(@Param("partDetailsId") Long partDetailsId,
			@Param("actualYieldLossRatio") BigDecimal actualYieldLossRatio);

}
