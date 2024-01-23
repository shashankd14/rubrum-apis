package com.steel.product.application.dao;

import com.steel.product.application.entity.FGLabelEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FGLabelRepository extends JpaRepository<FGLabelEntity, Integer> {

	@Query("SELECT MAX(labelSeq) FROM FGLabelEntity where inwardentryid=:iwardId")
	Integer getLastReqId(@Param("iwardId") Integer iwardId);

	@Query("select distinct fg from FGLabelEntity fg where fg.inwardentryid = :inwardId order by labelSeq desc")
	List<Object[]> getFGLabels(@Param("inwardId") Integer inwardId);

}
