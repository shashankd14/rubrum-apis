package com.steel.product.application.dao;

import com.steel.product.application.entity.YieldLossRatioMasterEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface YieldLossRatioMasterRepository extends JpaRepository<YieldLossRatioMasterEntity, Integer> {

	@Query("select pc from YieldLossRatioMasterEntity pc where pc.isDeleted is false and pc.partyId = :partyId and pc.processId = :processId and :rangeValue between pc.lossRatioPercentageFrom and pc.lossRatioPercentageTo")
	List<YieldLossRatioMasterEntity> validateRange(@Param("partyId") Integer partyId, @Param("processId") Integer processId, @Param("rangeValue") BigDecimal rangeValue);

	@Query("select pc from YieldLossRatioMasterEntity pc where pc.isDeleted is false and pc.partyId = :partyId and pc.processId = :processId and :rangeValue between pc.lossRatioPercentageFrom and pc.lossRatioPercentageTo and pc.ylrId not in :ylrId")
	List<YieldLossRatioMasterEntity> validateRangeInUpdate(@Param("partyId") Integer partyId, @Param("processId") Integer processId, @Param("rangeValue") BigDecimal rangeValue, @Param("ylrId") Integer ylrId);

	@Query(value = "SELECT ylr.ylr_id, ylr.party_id, ylr.process_id, ylr.loss_ratio_percentage_from,  ylr.loss_ratio_percentage_to,  ylr.comments, "
			+ " party.partyname, proce.processname\r\n"
			+ " FROM yield_loss_ratio_master ylr, product_tblpartydetails party, product_process proce \r\n"
			+ " where is_deleted = 0 and party.npartyid =ylr.party_id and proce.processid =ylr.process_id "
			+ " and ylr.party_id = ifnull(:partyId, ylr.party_id) \r\n"
			+ " and ylr.process_id = ifnull(:processId, ylr.process_id) \r\n"
			+ " and ylr.ylr_id = ifnull(:ylrId, ylr.ylr_id) \r\n"
			+ " order by ylr.ylr_id desc", 
		countQuery = "SELECT count( ylr.ylr_id)"
			+ " FROM yield_loss_ratio_master ylr, product_tblpartydetails party, product_process proce \r\n"
			+ " where is_deleted = 0 and party.npartyid =ylr.party_id and proce.processid =ylr.process_id"
			+ " and ylr.process_id = ifnull(:processId, ylr.process_id) \r\n"
			+ " and ylr.ylr_id = ifnull(:ylrId, ylr.ylr_id) \r\n"
			+ " and ylr.party_id = ifnull(:partyId, ylr.party_id) \r\n", 
		nativeQuery = true)
	Page<Object[]> findAll(@Param("partyId") Integer partyId, @Param("processId") Integer processId,
			@Param("ylrId") Integer ylrId, Pageable pageable);

	@Modifying
	@Transactional
	@Query("update YieldLossRatioMasterEntity inw set inw.isDeleted = true, inw.updatedBy=:userId, inw.updatedOn=CURRENT_TIMESTAMP where inw.ylrId in :itemIds")
	void deleteData(@Param("itemIds") List<Integer> itemIds, @Param("userId") Integer userId);
	
 
}
