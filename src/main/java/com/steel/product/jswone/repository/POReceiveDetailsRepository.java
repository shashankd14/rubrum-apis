
package com.steel.product.jswone.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.jswone.entity.POReceiveDetailsEntity;

@Repository
public interface POReceiveDetailsRepository extends JpaRepository<POReceiveDetailsEntity, Integer> {

	POReceiveDetailsEntity findByPoReference(String poReference);

	@Query(value = "select po.po_reference, po.warehouse_id from jsw_warehouse_location_map map, jsw_po_receive_dtls po "
			+ "where map.warehouse_id=po.warehouse_id and po.po_status='Active' and map.location_id=:locationId  ", nativeQuery = true)
	List<Object[]> locationwisePOList(@Param("locationId") int locationId);

}
