
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.BrandMasterJswEntity;

@Repository
public interface BrandMasterJswRepository extends JpaRepository<BrandMasterJswEntity, Integer> {

	List<BrandMasterJswEntity> findByBrandName(String leafcategoryName);

	List<BrandMasterJswEntity> findByLeafcategoryId(Integer leafcategoryId);
}
