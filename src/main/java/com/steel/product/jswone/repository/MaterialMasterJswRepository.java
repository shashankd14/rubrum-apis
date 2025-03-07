package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialMasterJswRepository extends JpaRepository<MaterialMasterJswEntity, Integer> {
	
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
			+ " material.hsn,   "
			+ " material.tax,   "
			+ " material.variant_key ,material.category_id "
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
			+ " where 1=1 and material.category_id=coalesce(:categoryId ,material.category_id)  "
			+ " and material.subcategory_id=coalesce(:subcategoryId ,material.subcategory_id)  "
			+ " and material.leafcategory_id=coalesce(:leafcategoryId ,material.leafcategory_id)  "
			+ " and material.brand_id=coalesce(:brandId ,material.brand_id)  "
			+ " and material.producttype_id=coalesce(:producttypeId ,material.producttype_id)  "
			+ " and material.grade_id=coalesce(:gradeId ,material.grade_id) "
			+ " and material.subgrade_id=coalesce(:subgradeId ,material.subgrade_id) "
			+ " and material.form_id=coalesce(:formId ,material.form_id) "
			+ " and material.uom_id=coalesce(:uomId ,material.uom_id) "
			+ " and material.surfacetype_id=coalesce(:surfacetypeId ,material.surfacetype_id) "
			+ " and material.coatingtype_id=coalesce(:coatingtypeId ,material.coatingtype_id) "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then "
			+ " ( material.mm_id like %:searchText% or material.mm_description like %:searchText% or "
			+ " material.variant_key like %:searchText% or material.diameter like %:searchText% or "
			+ " category.category_name like %:searchText% or subcat.subcategory_name like %:searchText% or "
			+ " leaf.leafcategory_name like %:searchText% or brand.brand_name like %:searchText%  or "
			+ " product.product_name like %:searchText% or "
			+ " grade.grade_name like %:searchText% or subgrade.subgrade_name like %:searchText% or"
			+ " form.form_name like %:searchText% or uom.uom_name like %:searchText% or "  
			+ " surface.surfacetype_name like %:searchText% or coating.coatingtype like %:searchText%  "
			+ " ) else 1=1 end order by material_id desc"  ,
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
			+ " where 1=1 and material.category_id=coalesce(:categoryId ,material.category_id)  "
			+ " and material.subcategory_id=coalesce(:subcategoryId , material.subcategory_id)  "
			+ " and material.leafcategory_id=coalesce(:leafcategoryId ,material.leafcategory_id)  "
			+ " and material.brand_id=coalesce(:brandId ,material.brand_id)  "
			+ " and material.producttype_id=coalesce(:producttypeId ,material.producttype_id)  "
			+ " and material.grade_id=coalesce(:gradeId ,material.grade_id) "
			+ " and material.subgrade_id=coalesce(:subgradeId ,material.subgrade_id) "
			+ " and material.form_id=coalesce(:formId ,material.form_id) "
			+ " and material.uom_id=coalesce(:uomId ,material.uom_id) "
			+ " and material.surfacetype_id=coalesce(:surfacetypeId ,material.surfacetype_id) "
			+ " and material.coatingtype_id=coalesce(:coatingtypeId ,material.coatingtype_id) "
			+ " and case when :searchText is not null and LENGTH(:searchText) >0 then "
			+ " ( material.mm_id like %:searchText% or material.mm_description like %:searchText% or "
			+ " material.variant_key like %:searchText% or material.diameter like %:searchText% or "
			+ " category.category_name like %:searchText% or subcat.subcategory_name like %:searchText% or "
			+ " leaf.leafcategory_name like %:searchText% or brand.brand_name like %:searchText%  or "
			+ " product.product_name like %:searchText% or "
			+ " grade.grade_name like %:searchText% or subgrade.subgrade_name like %:searchText% or"
			+ " form.form_name like %:searchText% or uom.uom_name like %:searchText% or "  
			+ " surface.surfacetype_name like %:searchText% or coating.coatingtype like %:searchText%  "
			+ " ) else 1=1 end", nativeQuery = true)
	Page<Object[]> materialSearch(@Param("searchText") String searchText, 
			@Param("categoryId") Integer categoryId,
			@Param("subcategoryId") Integer subcategoryId, 
			@Param("leafcategoryId") Integer leafcategoryId,
			@Param("brandId") Integer brandId, 
			@Param("producttypeId") Integer producttypeId,
			@Param("gradeId") Integer gradeId, 
			@Param("subgradeId") Integer subgradeId,
			@Param("formId") Integer formId, 
			@Param("uomId") Integer uomId, 
			@Param("surfacetypeId") Integer surfacetypeId,
			@Param("coatingtypeId") Integer coatingtypeId, 
			Pageable pageable);
	
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
			+ " material.hsn,   "
			+ " material.tax,   "
			+ " material.variant_key ,material.category_id "
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
			+ " where 1=1 and material.mm_id like %:mmid% ", nativeQuery = true)
	Page<Object[]> materialSearchBymmid(@Param("mmid") String mmid,  Pageable pageable);
}
