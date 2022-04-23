package com.steel.product.application.service;

import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;

import java.util.List;
import java.util.Set;

public interface EndUserTagsService {

    List<EndUserTagsResponse> getAllEndUserTags();

    EndUserTagsEntity getEndUserTagsById(int paramInt);

    List<EndUserTagsEntity> findAllByTagIdIn(List<Integer> packetClassificationIds);

    Set<EndUserTagsEntity> findByTagName(List<String> classificationNames);

    List<EndUserTagsResponse> getAllEndUserTagsByPartyId(Integer partyId);

    String saveEndUserTags(List<EndUserTagsRequest> packetClassificationRequests);

    String updateEndUserTags(EndUserTagsRequest packetClassificationRequests);

    String deleteEndUserTags(int tagId);

}
