package com.steel.product.application.dao;

import com.steel.product.application.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyDetailsRepository extends JpaRepository<Party, Integer> {
	
}
