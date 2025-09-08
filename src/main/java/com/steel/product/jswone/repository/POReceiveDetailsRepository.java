
package com.steel.product.jswone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.POReceiveDetailsEntity;

@Repository
public interface POReceiveDetailsRepository extends JpaRepository<POReceiveDetailsEntity, Integer> {

	POReceiveDetailsEntity findByPoReference(String poReference);
}
