package com.steel.product.application.dao;

import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryDetailsRepository extends JpaRepository<DeliveryDetails, Integer> {

    @Query(" from Instruction where deliveryId != NULL AND status = 4")
    public List<Instruction> deliveredItems();

    @Query(" from Instruction where deliveryId =:deliveryId")
    public List<Instruction> deliveredItemsById(@Param("deliveryId") int deliveryId);

    @Query("select ins from Instruction ins join ins.deliveryDetails dd where dd.deliveryId != 0")
    public List<Instruction> findAllDeliveries();
}