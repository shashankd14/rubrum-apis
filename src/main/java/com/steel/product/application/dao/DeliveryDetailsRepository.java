package com.steel.product.application.dao;

import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryDetailsRepository extends JpaRepository<DeliveryDetails, Integer> {

    @Query(" from Instruction where deliveryId != NULL AND status = 4")
    public List<Instruction> deliveredItems();

    @Query(" from Instruction where deliveryId =:deliveryId")
    public List<Instruction> deliveredItemsById(@Param("deliveryId") int deliveryId);

    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null and "
    		+ " ( inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
    		+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) group by inw")
    public Page<DeliveryDetails> findAllDeliveries(@Param("searchText") String searchText, Pageable pageable);
    
    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null group by inw, dd")
    public List<DeliveryDetails> findAllDeliveries();

    @Query("select ins from Instruction ins left join fetch ins.parentInstruction inner join fetch ins.deliveryDetails dd where dd.deliveryId = :deliveryId")
    List<Instruction> findInstructionsByDeliveryId(@Param("deliveryId")Integer deliveryId);//delete delivery

//    @Query("select sum(ins.actualWeight) from Instruction ins where ins.inwardId.inwardEntryId = :inwardId group by ins.inwardId")
    @Query("select ins.actualWeight from Instruction ins where ins.inwardId.inwardEntryId = :inwardId and instructionId = :instructionId")
    public Float findInstructionByInwardIdAndInstructionId(@Param("inwardId")Integer inwardId,@Param("instructionId")Integer instructionId);
}