package com.steel.product.application.service;

import com.steel.product.application.dao.EndUserTagsRepository;
import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.entity.Material;
import com.steel.product.application.mapper.EndUserTagsMapper;
import com.steel.product.application.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class EndUserTagsServiceImpl implements EndUserTagsService {

	private EndUserTagsRepository endUserTagsRepository;

	private EndUserTagsMapper endUserTagsMapper;

	private CommonUtil commonUtil;

	@Autowired
	public EndUserTagsServiceImpl(EndUserTagsRepository endUserTagsRepository, EndUserTagsMapper endUserTagsMapper,
			CommonUtil commonUtil) {
		this.endUserTagsRepository = endUserTagsRepository;
		this.endUserTagsMapper = endUserTagsMapper;
		this.commonUtil = commonUtil;
	}

	@Override
	public List<EndUserTagsResponse> getAllEndUserTags() {
		List<EndUserTagsEntity> list = endUserTagsRepository.findAllEndUserTags(commonUtil.getLocationWiseMappedUserIds());
		return endUserTagsMapper.toList(list);
	}

	@Override
	public EndUserTagsEntity getEndUserTagsById(int endUserTagsEntityById) {
		log.info("Hi endUserTagsEntityById ==  "+endUserTagsEntityById);
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
		int userId = commonUtil.getUserId();
		List<EndUserTagsEntity> list = endUserTagsMapper.requestToEntity(endUserTagsRequests);

		for (EndUserTagsEntity entity : list) {
			EndUserTagsEntity oldEndUserTagsEntity = endUserTagsRepository.findByTagName(entity.getTagName());
			if (oldEndUserTagsEntity != null && oldEndUserTagsEntity.getTagName() != null
					&& oldEndUserTagsEntity.getTagName().equalsIgnoreCase(entity.getTagName())) {
				return "Entered End user TagName already exists";
			}
			endUserTagsRepository.save(entity);
		}
		return "Saved OK !!";
	}

	@Override
	public String updateEndUserTags(EndUserTagsRequest endUserTagsRequest) {
		EndUserTagsEntity oldEndUserTagsEntity = endUserTagsRepository.findByTagName(endUserTagsRequest.getTagName());
		if (oldEndUserTagsEntity != null && oldEndUserTagsEntity.getTagName() != null && endUserTagsRequest.getTagId() != oldEndUserTagsEntity.getTagId()) {
			return "Entered End user TagName already exists";
		}
		EndUserTagsEntity endUserTagsEntity = endUserTagsRepository.findByTagId(endUserTagsRequest.getTagId());
		endUserTagsEntity.setUpdatedby( endUserTagsRequest.getCreatedby());
		endUserTagsEntity.setTagName( endUserTagsRequest.getTagName());
		endUserTagsRepository.save(endUserTagsEntity);
		return "Enduser tag updated Successfully..!";
	}

	@Override
	public ResponseEntity<Object> deleteEndUserTags(int tagId) {
		try {
			endUserTagsRepository.deleteById(tagId);
		} catch (Exception e) {
			return new ResponseEntity("Selected End User Tag is being used..!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity("Deleted Successfully..!", HttpStatus.OK);
	}

}
