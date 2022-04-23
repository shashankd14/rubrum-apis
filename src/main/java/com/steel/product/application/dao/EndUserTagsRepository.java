package com.steel.product.application.dao;

import com.steel.product.application.entity.EndUserTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EndUserTagsRepository extends JpaRepository<EndUserTagsEntity, Integer> {

	List<EndUserTagsEntity> findAllByTagIdIn(List<Integer> tagIds);

	Set<EndUserTagsEntity> findAllByTagNameIn(List<String> tagNames);

	EndUserTagsEntity findByTagName(String tagName);

	@Query("select pc from EndUserTagsEntity pc left join fetch pc.parties p where p.nPartyId = :partyId")
	List<EndUserTagsEntity> findByPartyId(@Param("partyId") Integer partyId);
}
