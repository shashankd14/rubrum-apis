package com.steel.product.application.dao;

import com.steel.product.application.entity.InwardEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;
import static org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH;

@Repository
public interface InwardEntryRepository extends JpaRepository<InwardEntry, Integer> {

    @Query(nativeQuery = true, value = "select * from aspen.product_tblinwardentry where nPartyId= :partyId order by dReceivedDate ")
    List<InwardEntry> getInwardEntriesByPartyId(@Param("partyId") Integer paramInteger);

    @Query("select inw from InwardEntry inw where (inw.coilNumber like %:searchText% or "
    		+ " inw.customerBatchId like %:searchText% or inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) "
    		+ " order by inwardEntryId desc")
    Page<InwardEntry> findAll(@Param("searchText") String searchText, Pageable pageable);
    
	@Query("select inw from InwardEntry inw where (inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
			+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId=:partyId order by inwardEntryId desc")
	Page<InwardEntry> findAll(@Param("searchText") String searchText, @Param("partyId") int partyId, Pageable pageable);
    
    @Query("select inw from InwardEntry inw order by inwardEntryId desc")
    List<InwardEntry> findAll();

    @Query(nativeQuery = true, value = "SELECT coilNumber FROM product_tblinwardentry WHERE coilNumber = :coilNumber")
    String isCoilNumberPresent(@Param("coilNumber") String paramString);

    @Query(nativeQuery = true, value = "SELECT customerbatchid FROM product_tblinwardentry WHERE customerbatchid = :customerbatchid limit 1")
    String isCustomerBatchIdPresent(@Param("customerbatchid") String customerbatchId);

    @Query("select inw from InwardEntry inw left join fetch inw.instructions ins where inw.coilNumber = :coilNumber order by ins.instructionId desc")
    <T> Optional<InwardEntry> findByCoilNumber(@Param("coilNumber")String coilNumber);

    @Query("select DISTINCT(inw) from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins join fetch ins.deliveryDetails dd where dd is not null" +
            " and ins.instructionId in :instructionIds and" +
            " ins.status.statusId = 4")
    public List<InwardEntry> findDeliveryItemsByInstructionIds(@Param("instructionIds")List<Integer> instructionIds);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade where inw.inwardEntryId = :inwardId")
    public Optional<InwardEntry> findById(@Param("inwardId")Integer inwardId);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins join fetch ins.childInstructions")
    List<InwardEntry> findAllInwards();

    @Query("select distinct inw from InwardEntry inw left join fetch inw.party party left join fetch inw.material mat " +
            "left join fetch inw.materialGrade matG left join fetch inw.instructions ins left join fetch ins.packetClassification " +
            "where party.nPartyId = :partyId and inw.isDeleted is false and inw.status < 4 and inw.inStockWeight > 0 and ins.groupId is null" +
            " or ins is null order by inw.inwardEntryId")
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "10"),
            @QueryHint(name = HINT_PASS_DISTINCT_THROUGH,value = "false")
    })
    List<InwardEntry> findInwardByPartyId(Integer partyId);
//    and ins.isDeleted is false
}

//@Query("select distinct inw,mat,grade from InwardEntry inw left join fetch inw.party left join fetch inw.instructions ins left join" +
//        " inw.material mat left join inw.materialGrade grade left join inw.status st" +
//        " where inw.party.id = :partyId and st.statusId < 4 and inw.isDeleted is false and ins.isDeleted is false " +
//        "and (ins.status.statusId < 4 or ins.status.statusId >= 4 and inw.inStockWeight > 0)" +
//        " or ins is null order by inw.inwardEntryId")
