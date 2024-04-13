package com.steel.product.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.trading.entity.InwardTradingEntity;

@Repository
public interface InwardTradingRepository extends JpaRepository<InwardTradingEntity, Integer> {

}
