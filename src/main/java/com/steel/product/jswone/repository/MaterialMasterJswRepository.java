package com.steel.product.jswone.repository;

import com.steel.product.jswone.entity.MaterialMasterJswEntity;

import java.math.BigDecimal;

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
			+ " CAST(material.hsn AS CHAR) ,   "
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
			+ " where 1=1 "
			+ " and material.category_id=case when :categoryId >0 then :categoryId else material.category_id end"
			+ " and material.subcategory_id=case when :subcategoryId >0 then :subcategoryId else material.subcategory_id end"
			+ " and material.leafcategory_id=case when :leafcategoryId >0 then :leafcategoryId else material.leafcategory_id end "
			+ " and material.brand_id=case when :brandId >0 then :brandId else material.brand_id end "
			+ " and material.producttype_id=case when :producttypeId >0 then :producttypeId else material.producttype_id end "
			+ " and material.grade_id=case when :gradeId >0 then :gradeId else material.grade_id end "
			+ " and material.subgrade_id=case when :subgradeId >0 then :subgradeId else material.subgrade_id end "
			+ " and material.form_id=case when :formId >0 then :formId else material.form_id end "
			+ " and material.uom_id=case when :uomId >0 then :uomId else material.uom_id end "
			+ " and material.surfacetype_id=case when :surfacetypeId >0 then :surfacetypeId else material.surfacetype_id end "
			+ " and material.coatingtype_id=case when :coatingtypeId >0 then :coatingtypeId else material.coatingtype_id end "
			+ " and material.length=case when :length >0 then :length else material.length end "
			+ " and material.width=case when :width >0 then :width else material.width end "
			+ " and material.thickness=case when :thickness >0 then :thickness else material.thickness end "
			+ " order by material_id desc"  ,
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
				+ " where 1=1 "
				+ " and material.category_id=case when :categoryId >0 then :categoryId else material.category_id end"
				+ " and material.subcategory_id=case when :subcategoryId >0 then :subcategoryId else material.subcategory_id end"
				+ " and material.leafcategory_id=case when :leafcategoryId >0 then :leafcategoryId else material.leafcategory_id end "
				+ " and material.brand_id=case when :brandId >0 then :brandId else material.brand_id end "
				+ " and material.producttype_id=case when :producttypeId >0 then :producttypeId else material.producttype_id end "
				+ " and material.grade_id=case when :gradeId >0 then :gradeId else material.grade_id end "
				+ " and material.subgrade_id=case when :subgradeId >0 then :subgradeId else material.subgrade_id end "
				+ " and material.form_id=case when :formId >0 then :formId else material.form_id end "
				+ " and material.uom_id=case when :uomId >0 then :uomId else material.uom_id end "
				+ " and material.surfacetype_id=case when :surfacetypeId >0 then :surfacetypeId else material.surfacetype_id end "
				+ " and material.coatingtype_id=case when :coatingtypeId >0 then :coatingtypeId else material.coatingtype_id end "
				+ " and material.length=case when :length >0 then :length else material.length end "
				+ " and material.width=case when :width >0 then :width else material.width end "
				+ " and material.thickness=case when :thickness >0 then :thickness else material.thickness end "
			+ " ", 
		nativeQuery = true)
	Page<Object[]> materialSearch( 
			@Param("thickness") BigDecimal thickness, 
			@Param("width") BigDecimal width, 
			@Param("length") BigDecimal length, 
			//@Param("oDiameter") BigDecimal oDiameter, 
			//@Param("nb") BigDecimal nb, 
			//@Param("iDiameter") BigDecimal iDiameter, 			
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
			+ " where 1=1 and material.mm_id like %:mmid% ", nativeQuery = true)
	Page<Object[]> materialSearchBymmid(@Param("mmid") String mmid,  Pageable pageable);
}
