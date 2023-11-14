package com.steel.product.application.dao;

import com.steel.product.application.entity.LaminationChargesEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LaminationChargesRepository extends JpaRepository<LaminationChargesEntity, Integer> {

	List<LaminationChargesEntity> findByPartyIdAndLaminationDetailsId(Integer partyId, Integer laminationDetailsId);

	@Query(" select lamcharges.laminationId, lamcharges.partyId, lamcharges.laminationDetailsId, lamcharges.charges, "
			+ " party.partyName, lami.laminationDetailsDesc, lamcharges.createdBy, lamcharges.updatedBy,"
			+ " lamcharges.createdOn, lamcharges.updatedOn "
			+ " from LaminationChargesEntity lamcharges, Party party, LaminationStaticDataEntity lami"
			+ " where lamcharges.partyId = party.nPartyId and lami.laminationDetailsId = lamcharges.laminationDetailsId "
			+ " and lamcharges.partyId = :partyId")
	List<Object[]> findByPartyId(@Param("partyId") Integer partyId);

	@Query(" select lamcharges.laminationId, lamcharges.partyId, lamcharges.laminationDetailsId, lamcharges.charges, "
			+ " party.partyName, lami.laminationDetailsDesc, lamcharges.createdBy, lamcharges.updatedBy,"
			+ " lamcharges.createdOn, lamcharges.updatedOn "
			+ " from LaminationChargesEntity lamcharges, Party party, LaminationStaticDataEntity lami"
			+ " where lamcharges.partyId = party.nPartyId and lami.laminationDetailsId = lamcharges.laminationDetailsId "
			+ " and lamcharges.laminationId = :laminationId")
	List<Object[]> findByLaminationId(@Param("laminationId") Integer laminationId);

	@Query(" select lamcharges.laminationId, lamcharges.partyId, lamcharges.laminationDetailsId, lamcharges.charges, "
			+ " party.partyName, lami.laminationDetailsDesc, lamcharges.createdBy, lamcharges.updatedBy,"
			+ " lamcharges.createdOn, lamcharges.updatedOn "
			+ " from LaminationChargesEntity lamcharges, Party party, LaminationStaticDataEntity lami"
			+ " where lamcharges.partyId = party.nPartyId and lami.laminationDetailsId = lamcharges.laminationDetailsId")
	List<Object[]> findAll1();

}
