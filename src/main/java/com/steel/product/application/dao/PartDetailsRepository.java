package com.steel.product.application.dao;

import com.steel.product.application.entity.PartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
			+ "	actuallength, actualweight, enduser_tag_name, plannednoofpieces, parentcoilnumber, process_status "
			+ " FROM label_print_vw where stts=:stts and part_details_id=:planId ", nativeQuery = true)
	List<Object[]> wipLabelData(@Param("stts") Integer stts, @Param("planId") String planId);

}
