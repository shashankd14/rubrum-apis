package com.steel.product.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steel.product.application.entity.InwardDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardDocRepository extends JpaRepository<InwardDoc, Integer> {

}
