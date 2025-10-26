
package com.steel.product.jswone.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.steel.product.jswone.entity.SOReceiveDetailsEntity;

@Repository
public interface SOReceiveDetailsRepository extends JpaRepository<SOReceiveDetailsEntity, Integer> {

	SOReceiveDetailsEntity findBySoNo(String poReference);

	@Query(value = "select so.so_no, so.so_id from jsw_warehouse_master map, jsw_so_receive_dtls so "
			+ "where map.ware_house_id=so.warehouse_id and map.party_id=:locationId  ", nativeQuery = true)
	List<Object[]> locationwiseSOList(@Param("locationId") int locationId);

}
