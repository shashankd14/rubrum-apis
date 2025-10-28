
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.UomMasterJswEntity;

@Repository
public interface UomMasterJswRepository extends JpaRepository<UomMasterJswEntity, Integer> {

	List<UomMasterJswEntity> findByUomName(String uomName);

	List<UomMasterJswEntity> findByProductId(Integer productId);

	@Query(value = "select distinct product.uomId, product.uomName from UomMasterJswEntity product where 1=1 ")
	List<Object[]> distinctValues();
}
