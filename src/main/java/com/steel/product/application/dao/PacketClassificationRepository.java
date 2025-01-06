package com.steel.product.application.dao;

import com.steel.product.application.entity.PacketClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PacketClassificationRepository extends JpaRepository<PacketClassification, Integer> {

	public List<PacketClassification> findAllByClassificationIdIn(List<Integer> packetClassificationIds);

	public Set<PacketClassification> findAllByClassificationNameIn(List<String> classificationNames);

	public PacketClassification findByClassificationName(String classificationName);

	PacketClassification findByClassificationId(Integer classificationId);
	
	@Query("select pc from PacketClassification pc left join fetch pc.parties p where p.nPartyId = :partyId")
	public List<PacketClassification> findByPartyId(@Param("partyId") Integer partyId);
	
	@Query("select mat from PacketClassification mat where mat.createdby in :userIds order by mat.classificationId desc")
	public List<PacketClassification> findAllClassificationTags(@Param("userIds") List<Integer> userIds);
}
