package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.mapper.PacketClassificationMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:33+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class PacketClassificationMapperImpl implements PacketClassificationMapper {

    @Override
    public PacketClassificationResponse toResponse(PacketClassification packetClassification) {
        if ( packetClassification == null ) {
            return null;
        }

        PacketClassificationResponse packetClassificationResponse = new PacketClassificationResponse();

        if ( packetClassification.getCreatedOn() != null ) {
            packetClassificationResponse.setCreatedOn( new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" ).format( packetClassification.getCreatedOn() ) );
        }
        if ( packetClassification.getUpdatedOn() != null ) {
            packetClassificationResponse.setUpdatedOn( new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" ).format( packetClassification.getUpdatedOn() ) );
        }
        packetClassificationResponse.setClassificationId( packetClassification.getClassificationId() );
        packetClassificationResponse.setClassificationName( packetClassification.getClassificationName() );

        return packetClassificationResponse;
    }

    @Override
    public List<PacketClassificationResponse> toList(List<PacketClassification> packetClassification) {
        if ( packetClassification == null ) {
            return null;
        }

        List<PacketClassificationResponse> list = new ArrayList<PacketClassificationResponse>( packetClassification.size() );
        for ( PacketClassification packetClassification1 : packetClassification ) {
            list.add( toResponse( packetClassification1 ) );
        }

        return list;
    }

    @Override
    public PacketClassificationRequest toEntity(PacketClassification packetClassification) {
        if ( packetClassification == null ) {
            return null;
        }

        PacketClassificationRequest packetClassificationRequest = new PacketClassificationRequest();

        packetClassificationRequest.setClassificationId( packetClassification.getClassificationId() );
        packetClassificationRequest.setClassificationName( packetClassification.getClassificationName() );

        return packetClassificationRequest;
    }

    @Override
    public PacketClassification toEntity(PacketClassificationRequest PacketClassificationRequest) {
        if ( PacketClassificationRequest == null ) {
            return null;
        }

        PacketClassification packetClassification = new PacketClassification();

        packetClassification.setClassificationId( PacketClassificationRequest.getClassificationId() );
        packetClassification.setClassificationName( PacketClassificationRequest.getClassificationName() );

        return packetClassification;
    }

    @Override
    public List<PacketClassification> requestToEntity(List<PacketClassificationRequest> packetClassificationRequest) {
        if ( packetClassificationRequest == null ) {
            return null;
        }

        List<PacketClassification> list = new ArrayList<PacketClassification>( packetClassificationRequest.size() );
        for ( PacketClassificationRequest packetClassificationRequest1 : packetClassificationRequest ) {
            list.add( toEntity( packetClassificationRequest1 ) );
        }

        return list;
    }
}
