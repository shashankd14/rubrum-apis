package com.steel.product.jswone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.POWiseMmidDetailsEntity;

@Repository
public interface POWiseMmisDetailsRepository extends JpaRepository<POWiseMmidDetailsEntity, Integer> {

	POWiseMmidDetailsEntity findByMmId(String sku);

}
