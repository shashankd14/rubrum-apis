package com.steel.product.application.dao;

import com.steel.product.application.entity.UserPartyMap;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserPartyMappingRepository extends CrudRepository<UserPartyMap, Integer> {

	public List<UserPartyMap> findByPartyId(Integer partyId);

}