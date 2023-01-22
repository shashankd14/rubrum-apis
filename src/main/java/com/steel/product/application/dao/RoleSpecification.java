package com.steel.product.application.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.steel.product.application.dto.admin.RoleSearch;
import com.steel.product.application.entity.RoleEntity;
import com.steel.product.application.util.ApplicationConstants;

public class RoleSpecification implements Specification<RoleEntity> {

	private static final long serialVersionUID = 1L;
	private RoleSearch criteria;

	public RoleSpecification(RoleSearch roleSearch) {
		this.criteria = roleSearch;
	}

	@Override
	public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		Path<String> roleId = root.get("roleId");
		Path<String> roleName = root.get("roleName");
		Path<String> activeStatus = root.get("activeStatus");

		final List<Predicate> predicates = new ArrayList<>();

		if (criteria.getRoleId() != 0) {
			predicates.add(cb.equal(roleId, criteria.getRoleId()));
		}

		if ((criteria.getRoleName() != null) && (!criteria.getRoleName().isEmpty())) {
			predicates.add(cb.like(cb.lower(roleName), "%" + criteria.getRoleName().toLowerCase() + "%"));
		}

		if ((criteria.getActiveStatus() != null)
				&& (criteria.getActiveStatus().equalsIgnoreCase(ApplicationConstants.ACTIVE_STATUS)
						|| criteria.getActiveStatus().equalsIgnoreCase(ApplicationConstants.INACTIVE_STATUS))) {
			predicates.add(cb.equal(activeStatus, criteria.getActiveStatus()));
		}

		query.orderBy(cb.desc(roleId));
		return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	}

}
