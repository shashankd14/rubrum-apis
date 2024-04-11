package com.steel.product.trading.repository;

import com.steel.product.trading.entity.InwardTradingEntityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardTradingRepository extends JpaRepository<InwardTradingEntityEntity, Integer> {

}
