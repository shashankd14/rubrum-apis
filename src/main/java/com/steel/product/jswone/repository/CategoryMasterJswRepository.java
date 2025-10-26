
package com.steel.product.jswone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.CategoryMasterJswEntity;

@Repository
public interface CategoryMasterJswRepository extends JpaRepository<CategoryMasterJswEntity, Integer> {

	@Query("select inw from CategoryMasterJswEntity inw where inw.categoryName = :categoryName and inw.categoryId not in :categoryId")
	List<CategoryMasterJswEntity> findByCategoryName(@Param("categoryName") String categoryName,
			@Param("categoryId") Integer categoryId);

	@Query("select inw from CategoryMasterJswEntity inw where inw.categoryName = :categoryName")
	List<CategoryMasterJswEntity> findByCategoryName(@Param("categoryName") String categoryName);

	@Query("select inw from CategoryMasterJswEntity inw where inw.categoryName like %:searchText%")
	Page<CategoryMasterJswEntity> findAllWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	@Query("select inw from CategoryMasterJswEntity inw where 1=1 ")
	Page<CategoryMasterJswEntity> findAll(Pageable pageable);

	Optional<CategoryMasterJswEntity> findByCategoryId(Integer id);

}
