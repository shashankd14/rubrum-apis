package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InwardEntryRepository extends JpaRepository<InwardEntry, Integer> {
    @Query(nativeQuery = true, value = "select * from aspen.product_tblinwardentry where nPartyId= :partyId order by dReceivedDate ")
    List<InwardEntry> getInwardEntriesByPartyId(@Param("partyId") Integer paramInteger);

    @Query(nativeQuery = true, value = "SELECT coilNumber FROM product_tblinwardentry WHERE coilNumber = :coilNumber")
    String isCoilNumberPresent(@Param("coilNumber") String paramString);

    @Query(nativeQuery = true, value = "SELECT customerbatchid FROM product_tblinwardentry WHERE customerbatchid = :customerbatchid limit 1")
    String isCustomerBatchIdPresent(@Param("customerbatchid") String customerbatchId);

    <T> Optional<InwardEntry> findByCoilNumber(String coilNumber);

    @Query("select DISTINCT(inw) from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins join fetch ins.deliveryDetails dd where dd is not null" +
            " and ins.instructionId in :instructionIds and" +
            " ins.status.statusId = 4")
    public List<InwardEntry> findDeliveryItemsByInstructionIds(@Param("instructionIds")List<Integer> instructionIds);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade where inw.inwardEntryId = :inwardId")
    public Optional<InwardEntry> findById(@Param("inwardId")Integer inwardId);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins " +
            "where inw.inwardEntryId = :inwardId and ins.parentGroupId = :groupId and ins.process.processId = :processId")
    public Optional<InwardEntry> fetchInwardWithCutInstructions(@Param("inwardId")Integer inwardId, @Param("groupId")Integer groupId, @Param("processId")Integer processId);

}
