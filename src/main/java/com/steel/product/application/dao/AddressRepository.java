package com.steel.product.application.dao;

import com.steel.product.application.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {}
