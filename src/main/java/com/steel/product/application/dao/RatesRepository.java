package com.steel.product.application.dao;

import com.steel.product.application.entity.Rates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatesRepository extends JpaRepository<Rates,Integer> {
}
