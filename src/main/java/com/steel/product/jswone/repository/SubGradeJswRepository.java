
package com.steel.product.jswone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.SubgradeMasterJswEntity;
import com.steel.product.jswone.response.SubGradeDTO;

@Repository
public interface SubGradeJswRepository extends JpaRepository<SubgradeMasterJswEntity, Integer> {

	List<SubgradeMasterJswEntity> findBySubgradeName(String subgradeName);

	List<SubgradeMasterJswEntity> findSubgradesByGradeId(Integer gradeId);

	@Query(value = "select distinct product.subgradeId, product.subgradeName from SubgradeMasterJswEntity product where 1=1 ")
	List<Object[]> distinctValues();
	  
	@Query("SELECT g.gradeId, g.gradeName, s.subgradeId, s.subgradeName "
	        + " FROM SubgradeMasterJswEntity s, GradeMasterJswEntity g, ProductMasterJswEntity prod"
	        + " WHERE g.gradeId = s.gradeId and g.productId = prod.productId and prod.brandId = :brandId"
	        + " ORDER BY g.gradeId")
	List<Object[]> gradeWithSubGradesByBrand(@Param("brandId") Integer brandId);

}
