package com.steel.product.application.dao;

import com.steel.product.application.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyDetailsRepository extends JpaRepository<Party, Integer> {

	@Query("select p,add1,add2 from Party p left join fetch p.packetClassificationTags left join p.address1 add1 left join p.address2 add2 order by p.nPartyId desc")
	List<Party> findAllParties();

	List<Party> findByPartyName(String partyName);
}
