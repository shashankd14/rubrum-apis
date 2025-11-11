package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterJswRepository 
	extends PagingAndSortingRepository<MaterialMasterJswEntity, Long>, JpaSpecificationExecutor<MaterialMasterJswEntity> {
	
	@Query(value = "select material.material_id, mm_id, mm_description, "
			+ " category_name,  "
			+ " material.subcategory_id, subcategory_name,   "
			+ " material.leafcategory_id,leafcategory_name,  "
			+ " material.brand_id, brand_name,  "
			+ " material.producttype_id, product_name,  "
			+ " material.grade_id, grade_name,  "
			+ " material.subgrade_id, subgrade_name,  "
			+ " material.form_id, form_name,  "
			+ " material.uom_id, uom_name,  "
			+ " material.surfacetype_id, surfacetype_name,  "
			+ " material.coatingtype_id, coatingtype,  "
			+ " material.diameter,   "
			+ " material.thickness,   "
			+ " material.width,   "
			+ " material.length,   "
			+ " material.spangle_type,   "
			+ " material.colour,   "
			+ " CAST(material.hsn AS CHAR),   "
			+ " material.tax,   "
			+ " CAST(material.variant_key AS CHAR), o_diameter,nb,i_diameter,material.category_id "
			+ " from jsw_material_master material    "
			+ " left outer join jsw_category_master category on category.category_id=material.category_id  "
			+ " left outer join jsw_subcategory_master subcat on subcat.subcategory_id=material.subcategory_id  "
			+ " left outer join jsw_leafcategory_master leaf on leaf.leafcategory_id =material.leafcategory_id  "
			+ " left outer join jsw_brand_master brand on brand.brand_id=material.brand_id  "
			+ " left outer join jsw_product_master product on product.product_id=material.producttype_id  "
			+ " left outer join jsw_grade_master grade on grade.grade_id=material.grade_id  "
			+ " left outer join jsw_subgrade_master subgrade on subgrade.subgrade_id=material.subgrade_id  "
			+ " left outer join jsw_form_master form on form.form_id=material.form_id  "
			+ " left outer join jsw_uom_master uom on uom.uom_id = material.uom_id  "
			+ " left outer join jsw_surfacetype_master surface on surface.surfacetype_id = material.surfacetype_id  "
			+ " left outer join jsw_coatingtype_master  coating on coating.coatingtype_id = material.coatingtype_id  "
			+ " where 1=1 and material.mm_id like %:mmid% order by material_id desc"  ,
		countQuery = "SELECT count(distinct material.material_id) "
			+ " from jsw_material_master material    "
			+ " left outer join jsw_category_master category on category.category_id=material.category_id  "
			+ " left outer join jsw_subcategory_master subcat on subcat.subcategory_id=material.subcategory_id  "
			+ " left outer join jsw_leafcategory_master leaf on leaf.leafcategory_id =material.leafcategory_id  "
			+ " left outer join jsw_brand_master brand on brand.brand_id=material.brand_id  "
			+ " left outer join jsw_product_master product on product.product_id=material.producttype_id  "
			+ " left outer join jsw_grade_master grade on grade.grade_id=material.grade_id  "
			+ " left outer join jsw_subgrade_master subgrade on subgrade.subgrade_id=material.subgrade_id  "
			+ " left outer join jsw_form_master form on form.form_id=material.form_id  "
			+ " left outer join jsw_uom_master uom on uom.uom_id = material.uom_id  "
			+ " left outer join jsw_surfacetype_master surface on surface.surfacetype_id = material.surfacetype_id  "
			+ " left outer join jsw_coatingtype_master  coating on coating.coatingtype_id = material.coatingtype_id  "
			+ " where 1=1  and material.mm_id like %:mmid% ", nativeQuery = true)
	Page<Object[]> materialSearchBymmid(@Param("mmid") String mmid,  Pageable pageable);

	List<MaterialMasterJswEntity> findByMmId(String mmid);

	MaterialMasterJswEntity findFirstByMmId(String mmid);
	
	@Query(value = "select producttype_id, grade_id, subgrade_id, brand_id, material, gradename, subgradename,brandname from ("
			+ " select distinct mat.producttype_id, mat.grade_id, mat.subgrade_id, mat.brand_id, "
			+ " (select product.product_name from jsw_product_master product where product.product_id=mat.producttype_id limit 1 ) as material, "
			+ " (select grade.grade_name from jsw_grade_master grade where grade.grade_id=mat.grade_id limit 1) as gradename, "
			+ " (select subgrade.subgrade_name from jsw_subgrade_master subgrade where subgrade.subgrade_id=mat.subgrade_id limit 1) as subgradename, "
			+ " (select brand.brand_name from jsw_brand_master brand where brand.brand_id=mat.brand_id limit 1) as brandname "
			+ " from product_tblinwardentry inw, jsw_material_master mat, product_tblpartydetails party"
			+ " where inw.isdeleted=0 and mat.mm_id=inw.mm_id and inw.npartyid=party.npartyid "
			+ "and (case when :status >0 then inw.vstatus=:status else 1=1 end ) "
			+ " ) product where 1=1 ", 
		nativeQuery = true)
	List<Object[]> listAllLocationWiseInwards(@Param("status") int status);

	void deleteByMaterialId(Integer materaiId); 
}
