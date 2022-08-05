package com.steel.product.application.dao;

import com.steel.product.application.entity.Party;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartyDetailsRepository extends JpaRepository<Party, Integer> {

	@Query("select party from Party party order by nPartyId desc")
	List<Party> findAllParties();

	@Query("select party from Party party order by nPartyId desc")
	Page<Party> findAllParties(Pageable pageable);

	List<Party> findByPartyName(String partyName);
}
