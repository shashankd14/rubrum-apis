package com.steel.product.application.dao;

import com.steel.product.application.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {}
