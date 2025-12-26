package com.steel.product.jswone.repository;

import java.util.List;

import javax.persistence.Column;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.InwardFileDataEntity;

@Repository
public interface InwardFiledataRepository extends JpaRepository<InwardFileDataEntity, Integer> {

	@Query("select inw from InwardFileDataEntity inw where inw.batchnumber not in (select mm.batchNumber from InwardEntry mm)")
	List<InwardFileDataEntity> findAll();

	@Query("select inw from InwardFileDataEntity inw where inw.serialNo =:serialNo")
	List<InwardFileDataEntity> findAll(@Param("serialNo") Integer serialNo);

	InwardFileDataEntity findFirstByBatchnumber(String batchnumber);

	@Query("SELECT COALESCE(MAX(e.serialNo), 0) + 1 FROM InwardFileDataEntity e")
	Integer findMaxValue();

	@Modifying
	@Transactional
	@Query("update InwardFileDataEntity set inwardEntryId=:inwardEntryId, inwardCreationStatus = :inwardCreationStatus where batchnumber= :batchnumber ")
	public void updateStatus(@Param("inwardEntryId") int inwardEntryId, @Param("inwardCreationStatus") String inwardCreationStatus, @Param("batchnumber") String batchnumber);

}
