package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.mapper.EndUserTagsMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:31+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class EndUserTagsMapperImpl implements EndUserTagsMapper {

    @Override
    public EndUserTagsResponse toResponse(EndUserTagsEntity endUserTagsEntity) {
        if ( endUserTagsEntity == null ) {
            return null;
        }

        EndUserTagsResponse endUserTagsResponse = new EndUserTagsResponse();

        if ( endUserTagsEntity.getCreatedOn() != null ) {
            endUserTagsResponse.setCreatedOn( new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" ).format( endUserTagsEntity.getCreatedOn() ) );
        }
        if ( endUserTagsEntity.getUpdatedOn() != null ) {
            endUserTagsResponse.setUpdatedOn( new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" ).format( endUserTagsEntity.getUpdatedOn() ) );
        }
        endUserTagsResponse.setTagId( endUserTagsEntity.getTagId() );
        endUserTagsResponse.setTagName( endUserTagsEntity.getTagName() );

        return endUserTagsResponse;
    }

    @Override
    public List<EndUserTagsResponse> toList(List<EndUserTagsEntity> endUserTagsEntity) {
        if ( endUserTagsEntity == null ) {
            return null;
        }

        List<EndUserTagsResponse> list = new ArrayList<EndUserTagsResponse>( endUserTagsEntity.size() );
        for ( EndUserTagsEntity endUserTagsEntity1 : endUserTagsEntity ) {
            list.add( toResponse( endUserTagsEntity1 ) );
        }

        return list;
    }

    @Override
    public EndUserTagsRequest toEntity(EndUserTagsEntity packetClassification) {
        if ( packetClassification == null ) {
            return null;
        }

        EndUserTagsRequest endUserTagsRequest = new EndUserTagsRequest();

        endUserTagsRequest.setTagId( packetClassification.getTagId() );
        endUserTagsRequest.setTagName( packetClassification.getTagName() );

        return endUserTagsRequest;
    }

    @Override
    public EndUserTagsEntity toEntity(EndUserTagsRequest packetClassification) {
        if ( packetClassification == null ) {
            return null;
        }

        EndUserTagsEntity endUserTagsEntity = new EndUserTagsEntity();

        endUserTagsEntity.setTagId( packetClassification.getTagId() );
        endUserTagsEntity.setTagName( packetClassification.getTagName() );

        return endUserTagsEntity;
    }

    @Override
    public List<EndUserTagsEntity> requestToEntity(List<EndUserTagsRequest> endUserTagsRequest) {
        if ( endUserTagsRequest == null ) {
            return null;
        }

        List<EndUserTagsEntity> list = new ArrayList<EndUserTagsEntity>( endUserTagsRequest.size() );
        for ( EndUserTagsRequest endUserTagsRequest1 : endUserTagsRequest ) {
            list.add( toEntity( endUserTagsRequest1 ) );
        }

        return list;
    }
}
