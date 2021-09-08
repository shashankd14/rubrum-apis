package com.steel.product.application.dao;

import com.steel.product.application.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> getByStatusIdIn(List<Integer> statusIds);
}
