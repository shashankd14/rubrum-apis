
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.CoatingtypeMasterJswEntity;

@Repository
public interface CoatingtypeMasterJswRepository extends JpaRepository<CoatingtypeMasterJswEntity, Integer> {
	List<CoatingtypeMasterJswEntity> findByCoatingtype(String coatingtype);

}
