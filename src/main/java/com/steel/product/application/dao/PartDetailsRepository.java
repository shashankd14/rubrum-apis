package com.steel.product.application.dao;

import com.steel.product.application.entity.PartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartDetailsRepository extends JpaRepository<PartDetails, Integer> {

    List<PartDetails> findAllByPartDetailsId(String partDetailsId);


}
