package com.steel.product.application.dao;

import com.steel.product.application.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository extends JpaRepository<Process, Integer > {
}
