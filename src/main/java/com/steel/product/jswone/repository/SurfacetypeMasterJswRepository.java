
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.SurfacetypeMasterJswEntity;

@Repository
public interface SurfacetypeMasterJswRepository extends JpaRepository<SurfacetypeMasterJswEntity, Integer> {

	List<SurfacetypeMasterJswEntity> findBySurfacetypeName(String surfacetypeName);

	List<SurfacetypeMasterJswEntity> findByProductId(Integer productId);
}
