package com.steel.product.application.service;

import com.steel.product.application.dao.EndUserTagsRepository;
import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.mapper.EndUserTagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EndUserTagsServiceImpl implements EndUserTagsService {

	private EndUserTagsRepository endUserTagsRepository;

	private EndUserTagsMapper endUserTagsMapper;

	@Autowired
	public EndUserTagsServiceImpl(EndUserTagsRepository endUserTagsRepository, EndUserTagsMapper endUserTagsMapper) {
		this.endUserTagsRepository = endUserTagsRepository;
		this.endUserTagsMapper = endUserTagsMapper;
	}

	@Override
	public List<EndUserTagsResponse> getAllEndUserTags() {
		List<EndUserTagsEntity> list = endUserTagsRepository.findAll();
		return endUserTagsMapper.toList(list);
	}

	@Override
	public EndUserTagsEntity getEndUserTagsById(int endUserTagsEntityById) {

		Optional<EndUserTagsEntity> result = endUserTagsRepository.findById(Integer.valueOf(endUserTagsEntityById));
		EndUserTagsEntity thePacketClassification = null;
		if (result.isPresent()) {
			thePacketClassification = result.get();
		} else {
			throw new RuntimeException("Did not find EndUserTagsEntity id - " + endUserTagsEntityById);
		}
		return thePacketClassification;
	}

	@Override
	public List<EndUserTagsEntity> findAllByTagIdIn(List<Integer> tagIds) {
		return endUserTagsRepository.findAllByTagIdIn(tagIds);
	}

	@Override
	public Set<EndUserTagsEntity> findByTagName(List<String> tagNames) {
		return endUserTagsRepository.findAllByTagNameIn(tagNames);
	}

	@Override
	public List<EndUserTagsResponse> getAllEndUserTagsByPartyId(Integer partyId) {
		List<EndUserTagsEntity> list = endUserTagsRepository.findByPartyId(partyId);
		return endUserTagsMapper.toList(list);
	}

	@Override
	public String saveEndUserTags(List<EndUserTagsRequest> endUserTagsRequests) {
		List<EndUserTagsEntity> list = endUserTagsMapper.requestToEntity(endUserTagsRequests);

		for (EndUserTagsEntity entity : list) {
			EndUserTagsEntity oldEndUserTagsEntity = endUserTagsRepository.findByTagName(entity.getTagName());
			if (oldEndUserTagsEntity != null && oldEndUserTagsEntity.getTagName() != null
					&& oldEndUserTagsEntity.getTagName().equalsIgnoreCase(entity.getTagName())) {
				return "Entered End user TagName already exists";
			}
			endUserTagsRepository.save(entity);
		}
		return "saved ok !!";
	}

	@Override
	public String updateEndUserTags(EndUserTagsRequest endUserTagsRequest) {
		EndUserTagsEntity oldEndUserTagsEntity = endUserTagsRepository.findByTagName(endUserTagsRequest.getTagName());
		if (oldEndUserTagsEntity != null && oldEndUserTagsEntity.getTagName() != null && endUserTagsRequest.getTagId() != oldEndUserTagsEntity.getTagId()) {
			return "Entered End user TagName already exists";
		}
		
		EndUserTagsEntity endUserTagsEntity = endUserTagsMapper.toEntity(endUserTagsRequest);
		endUserTagsRepository.save(endUserTagsEntity);
		return "Udated Successfully..!";
	}

	@Override
	public String deleteEndUserTags(int tagId) {
		try {
			endUserTagsRepository.deleteById(tagId);
		} catch (Exception e) {
			return "Selected End User Tag is being used..!";
		}
		return "Deleted Successfully..!";
	}

}
