package com.steel.product.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.steel.product.application.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

	@Query("select aa from Address aa where 1=1 order by aa.addressId desc")
	public List<Address> getAllAddress();
}
