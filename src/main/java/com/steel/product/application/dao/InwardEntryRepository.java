package com.steel.product.application.dao;

import com.steel.product.application.entity.InwardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InwardEntryRepository extends JpaRepository<InwardEntry, Integer> {
  @Query(nativeQuery = true, value = "select * from aspen.product_tblinwardentry where nPartyId= :partyId order by dReceivedDate ")
  List<InwardEntry> getInwardEntriesByPartyId(@Param("partyId") Integer paramInteger);
  
  @Query(nativeQuery = true, value = "SELECT coilNumber FROM product_tblinwardentry WHERE coilNumber = :coilNumber")
  String isCoilNumberPresent(@Param("coilNumber") String paramString);
  
  <T> Optional<InwardEntry> findByCoilNumber(String coilNumber);

  //List<InwardEntry> findInstructionByStatus();
}
