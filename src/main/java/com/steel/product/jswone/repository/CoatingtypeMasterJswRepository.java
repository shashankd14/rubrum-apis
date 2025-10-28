
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.CoatingtypeMasterJswEntity;

@Repository
public interface CoatingtypeMasterJswRepository extends JpaRepository<CoatingtypeMasterJswEntity, Integer> {
	List<CoatingtypeMasterJswEntity> findByCoatingtype(String coatingtype);

	List<CoatingtypeMasterJswEntity> findByProductId(Integer productId);

	@Query(value = "select distinct product.coatingtypeId, product.coatingtype from CoatingtypeMasterJswEntity product where 1=1 ")
	List<Object[]> distinctValues();

}
