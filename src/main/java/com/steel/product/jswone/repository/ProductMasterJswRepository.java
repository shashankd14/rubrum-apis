
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.ProductMasterJswEntity;

@Repository
public interface ProductMasterJswRepository extends JpaRepository<ProductMasterJswEntity, Integer> {
	List<ProductMasterJswEntity> findByProductName(String productName);

}
