
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.FormMasterJswEntity;

@Repository
public interface FormMasterJswRepository extends JpaRepository<FormMasterJswEntity, Integer> {
	List<FormMasterJswEntity> findByFormName(String formName);

	List<FormMasterJswEntity> findByProductId(Integer productId);

	@Query(value = "select distinct product.formId, product.formName from FormMasterJswEntity product where 1=1 ")
	List<Object[]> distinctValues();

}
