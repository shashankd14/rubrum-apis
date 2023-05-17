package com.steel.product.application.dao;

import com.steel.product.application.entity.Party;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartyDetailsRepository extends JpaRepository<Party, Integer> {

	@Query("select party from Party party order by nPartyId desc")
	List<Party> findAllParties();
	
	@Query("select party from Party party where party.nPartyId in :partyIds order by nPartyId desc")
	List<Party> findAllParties(@Param("partyIds") List<Integer> partyIds);
	
	@Query("select party from Party party order by nPartyId desc")
	Page<Party> findAllParties(Pageable pageable);
	
	@Query("select party from Party party where party.nPartyId in :partyIds order by nPartyId desc")
	Page<Party> findAllParties(Pageable pageable, @Param("partyIds") List<Integer> partyIds);

	List<Party> findByPartyName(String partyName);
}
