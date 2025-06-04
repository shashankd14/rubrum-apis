package com.steel.product.jswone.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.SalesOrderJswEntity;

@Repository
public interface SalesOrderJswRepository extends JpaRepository<SalesOrderJswEntity, Integer> { }
