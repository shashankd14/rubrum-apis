package com.steel.product.application.mapper;

import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EndUserTagsMapper {

    @Mapping(target = "createdOn",dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedOn",dateFormat = "dd-MM-yyyy HH:mm:ss")
    EndUserTagsResponse toResponse(EndUserTagsEntity endUserTagsEntity);
    
    List<EndUserTagsResponse> toList(List<EndUserTagsEntity> endUserTagsEntity);

    EndUserTagsRequest toEntity(EndUserTagsEntity packetClassification);
    EndUserTagsEntity toEntity(EndUserTagsRequest packetClassification);
    List<EndUserTagsEntity> requestToEntity(List<EndUserTagsRequest> endUserTagsRequest);
}
