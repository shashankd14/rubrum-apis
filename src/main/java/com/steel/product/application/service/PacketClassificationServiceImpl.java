package com.steel.product.application.service;

import com.steel.product.application.dao.PacketClassificationRepository;
import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.mapper.PacketClassificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PacketClassificationServiceImpl implements PacketClassificationService{

    private PacketClassificationRepository packetClassificationRepository;

    private PacketClassificationMapper packetClassificationMapper;

    @Autowired
    public PacketClassificationServiceImpl(PacketClassificationRepository packetClassificationRepository, PacketClassificationMapper packetClassificationMapper) {
        this.packetClassificationRepository = packetClassificationRepository;
        this.packetClassificationMapper = packetClassificationMapper;
    }

    @Override
    public List<PacketClassificationResponse> getAllPacketClassification() {
        List<PacketClassification> list = packetClassificationRepository.findAll();
        return packetClassificationMapper.toList(list);
    }

    @Override
    public PacketClassification getPacketClassificationById(int packetClassificationById) {

        Optional<PacketClassification> result = packetClassificationRepository.findById(Integer.valueOf(packetClassificationById));
        PacketClassification thePacketClassification = null;
        if (result.isPresent()) {
            thePacketClassification = result.get();
        } else {
            throw new RuntimeException("Did not find PacketClassification id - " + packetClassificationById);
        }
        return thePacketClassification;
    }

    @Override
    public List<PacketClassification> findAllByPacketClassificationIdIn(List<Integer> packetClassificationIds) {
        return packetClassificationRepository.findAllByClassificationIdIn(packetClassificationIds);
    }

    @Override
    public Set<PacketClassification> findByClassificationName(List<String> classificationNames) {
        return packetClassificationRepository.findAllByClassificationNameIn(classificationNames);
    }

    @Override
    public List<PacketClassificationResponse> getAllPacketClassificationByPartyId(Integer partyId) {
        List<PacketClassification> list = packetClassificationRepository.findByPartyId(partyId);
        return packetClassificationMapper.toList(list);
    }

	@Override
	public String savePacketClassifications(List<PacketClassificationRequest> packetClassificationRequest) {
		List<PacketClassification> list = packetClassificationMapper.requestToEntity(packetClassificationRequest);

		for (PacketClassification entity : list) {
			PacketClassification oldPacketClassificationEntity = packetClassificationRepository.findByClassificationName(entity.getClassificationName());
			if (oldPacketClassificationEntity != null && oldPacketClassificationEntity.getClassificationName() != null
					&& oldPacketClassificationEntity.getClassificationName().equalsIgnoreCase(entity.getClassificationName())) {
				return "Entered TagName already exists";
			}
			packetClassificationRepository.save(entity);
		}
		return "Saved Successfully !!";
	}

	@Override
	public String deleteEndUserTags(int classificationId) {
		try {
			packetClassificationRepository.deleteById(classificationId);
		} catch (Exception e) {
			return "Selected Tag is being used..!";
		}
		return "Deleted Successfully..!";
	}

	@Override
	public String updatePacketClassifications(PacketClassificationRequest packetClassificationRequest) {
		PacketClassification oldPacketClassificationEntity = packetClassificationRepository.findByClassificationName(packetClassificationRequest.getClassificationName());
		if (oldPacketClassificationEntity != null && oldPacketClassificationEntity.getClassificationName() != null 
				 && packetClassificationRequest.getClassificationId() != oldPacketClassificationEntity.getClassificationId()) {
			return "Entered End user TagName already exists";
		}
		
		PacketClassification endUserTagsEntity = packetClassificationMapper.toEntity(packetClassificationRequest);
		packetClassificationRepository.save(endUserTagsEntity);
		return "Udated Successfully..!";
	}
	

}
