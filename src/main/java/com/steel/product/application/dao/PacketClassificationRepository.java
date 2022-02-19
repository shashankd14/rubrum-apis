package com.steel.product.application.dao;


import com.steel.product.application.entity.PacketClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PacketClassificationRepository extends JpaRepository<PacketClassification, Integer>  {
    List<PacketClassification> findAllByClassificationIdIn(List<Integer> packetClassificationIds);
    Set<PacketClassification> findAllByClassificationNameIn(List<String> classificationNames);

    @Query("select pc from PacketClassification pc left join fetch pc.parties p where p.nPartyId = :partyId")
    List<PacketClassification> findByPartyId(@Param("partyId")Integer partyId);
}
