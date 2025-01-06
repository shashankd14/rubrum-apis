package com.steel.product.application.dao;

import com.steel.product.application.entity.UserLocationMappingEntity;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserLocationMappingRepository extends CrudRepository<UserLocationMappingEntity, Integer> {

	public List<UserLocationMappingEntity> findByLocationId(Integer locationId);

}