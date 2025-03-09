package com.steel.product.jswone.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.request.MaterialSearchPageRequest;

public class MaterialMasterJswSpecification implements Specification<MaterialMasterJswEntity> {

	private static final long serialVersionUID = 1793185027418394784L;
	private MaterialSearchPageRequest criteria;

	public MaterialMasterJswSpecification(MaterialSearchPageRequest search) {
		this.criteria = search;
	}

	@Override
	public Predicate toPredicate(Root<MaterialMasterJswEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		Path<BigDecimal> length = root.get("length");
		Path<BigDecimal> width = root.get("width");
		Path<BigDecimal> thickness = root.get("thickness");
		Path<BigDecimal> nb = root.get("nb");
		Path<BigDecimal> oDiameter = root.get("oDiameter");
		Path<BigDecimal> iDiameter = root.get("iDiameter");
		Path<Integer> categoryId = root.get("categoryId");
		Path<Integer> subcategoryId = root.get("subcategoryId");
		Path<Integer> leafcategoryId = root.get("leafcategoryId");
		Path<Integer> formId = root.get("formId");
		Path<Integer> producttypeId = root.get("producttypeId");
		Path<Integer> gradeId = root.get("gradeId");
		Path<Integer> subgradeId = root.get("subgradeId");
		Path<Integer> brandId = root.get("brandId");
		Path<Integer> surfacetypeId = root.get("surfacetypeId");
		Path<Integer> coatingtypeId = root.get("coatingtypeId");
		Path<Integer> uomId = root.get("uomId");

		final List<Predicate> predicates = new ArrayList<>();

		if (criteria.getCategoryId() != 0) {
			predicates.add(cb.equal(categoryId, criteria.getCategoryId()));
		}
		if (criteria.getSubcategoryId() != 0) {
			predicates.add(cb.equal(subcategoryId, criteria.getSubcategoryId()));
		}
		if (criteria.getFormId() != 0) {
			predicates.add(cb.equal(formId, criteria.getFormId()));
		}
		if (criteria.getLeafcategoryId() != 0) {
			predicates.add(cb.equal(leafcategoryId, criteria.getLeafcategoryId()));
		}
		if (criteria.getProducttypeId() != 0) {
			predicates.add(cb.equal(producttypeId, criteria.getProducttypeId()));
		}
		if (criteria.getGradeId() != 0) {
			predicates.add(cb.equal(gradeId, criteria.getGradeId()));
		}
		if (criteria.getSubgradeId() != 0) {
			predicates.add(cb.equal(subgradeId, criteria.getSubgradeId()));
		}
		if (criteria.getBrandId() != 0) {
			predicates.add(cb.equal(brandId, criteria.getBrandId()));
		}
		if (criteria.getSurfacetypeId() != 0) {
			predicates.add(cb.equal(surfacetypeId, criteria.getSurfacetypeId()));
		}
		if (criteria.getCoatingtypeId() != 0) {
			predicates.add(cb.equal(coatingtypeId, criteria.getCoatingtypeId()));
		}
		if (criteria.getUomId() != 0) {
			predicates.add(cb.equal(uomId, criteria.getUomId()));
		}
		if (criteria.getLength() != null && criteria.getLength().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(length, criteria.getLength()));
		}
		if (criteria.getWidth() != null && criteria.getWidth().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(width, criteria.getWidth()));
		}
		if (criteria.getThickness() != null && criteria.getThickness().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(thickness, criteria.getThickness()));
		}
		if (criteria.getNb() != null && criteria.getNb().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(nb, criteria.getNb()));
		}
		if (criteria.getODiameter() != null && criteria.getODiameter().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(oDiameter, criteria.getODiameter()));
		}
		if (criteria.getIDiameter() != null && criteria.getIDiameter().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(cb.equal(iDiameter, criteria.getIDiameter()));
		}
		query.orderBy(cb.desc(root.get("materialId")));
		return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	}

}
