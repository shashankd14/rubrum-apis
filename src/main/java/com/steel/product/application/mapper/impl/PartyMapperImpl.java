package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.address.AddressDto;
import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;
import com.steel.product.application.dto.quality.QualityPartyMappingResponse;
import com.steel.product.application.entity.Address;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.entity.PacketClassification;
import com.steel.product.application.entity.Party;
import com.steel.product.application.mapper.AddressMapper;
import com.steel.product.application.mapper.PacketClassificationMapper;
import com.steel.product.application.mapper.PartyMapper;
import com.steel.product.application.service.QualityService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:33+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class PartyMapperImpl implements PartyMapper {

    @Autowired
    private AddressMapper addressMapper;
    
    @Autowired
    private PacketClassificationMapper packetClassificationMapper;

    @Autowired
	private QualityService qualityService;
	
    @Override
    public Party toEntity(PartyDto partyDto) {
        if ( partyDto == null ) {
            return null;
        }

        Party party = new Party();

        party.setPartyName( partyDto.getPartyName() );
        party.setPartyNickname( partyDto.getPartyNickname() );
        party.setContactName( partyDto.getContactName() );
        party.setContactNumber( partyDto.getContactNumber() );
        party.setGstNumber( partyDto.getGstNumber() );
        party.setPanNumber( partyDto.getPanNumber() );
        party.setTanNumber( partyDto.getTanNumber() );
        party.setEmail1( partyDto.getEmail1() );
        party.setEmail2( partyDto.getEmail2() );
        party.setPhone1( partyDto.getPhone1() );
        party.setPhone2( partyDto.getPhone2() );
        party.setAddress1( addressMapper.toEntity( partyDto.getAddress1() ) );
        party.setAddress2( addressMapper.toEntity( partyDto.getAddress2() ) );
        return party;
    }

    @Override
    public PartyResponse toResponse(Party party) {
        if ( party == null ) {
            return null;
        }

        PartyResponse partyResponse = new PartyResponse();

        partyResponse.setnPartyId( party.getnPartyId() );
        partyResponse.setPartyName( party.getPartyName() );
        partyResponse.setPartyNickname( party.getPartyNickname() );
        partyResponse.setContactName( party.getContactName() );
        partyResponse.setContactNumber( party.getContactNumber() );
        partyResponse.setGstNumber( party.getGstNumber() );
        partyResponse.setPanNumber( party.getPanNumber() );
        partyResponse.setTanNumber( party.getTanNumber() );
        partyResponse.setEmail1( party.getEmail1() );
        partyResponse.setEmail2( party.getEmail2() );
        partyResponse.setPhone1( party.getPhone1() );
        partyResponse.setPhone2( party.getPhone2() );
        partyResponse.setAddress1( addressToAddressDto( party.getAddress1() ) );
        partyResponse.setAddress2( addressToAddressDto( party.getAddress2() ) );
        partyResponse.setPacketClassificationTags( packetClassificationSetToPacketClassificationResponseList( party.getPacketClassificationTags() ) );
        partyResponse.setEndUserTags( endUserTagsEntitySetToEndUserTagsResponseList( party.getEndUserTags() ) );
        partyResponse.setTemplateIdList(party.getTemplateIdList());

        return partyResponse;
    }

    @Override
    public List<PartyResponse> toResponseList(List<Party> party) {
        if ( party == null ) {
            return null;
        }

        List<PartyResponse> list = new ArrayList<PartyResponse>( party.size() );
        for ( Party party1 : party ) {
        	
        	try {
				List<QualityPartyMappingResponse> list11 = qualityService.getByPartyId(party1.getnPartyId());

				for (QualityPartyMappingResponse kk: list11 ) {
					QualityPartyMappingRequestNew kka =new QualityPartyMappingRequestNew();
					kka.setTemplateId(kk.getTemplateId());
					kka.setTemplateName(kk.getTemplateName());
					party1.getTemplateIdList().add(kka);
				}
			} catch (Exception e) {
				System.out.println("template data not available = "+party1.getnPartyId());
			}
        	
            list.add( toResponse( party1 ) );
        }

        return list;
    }

    protected EndUserTagsEntity endUserTagsRequestToEndUserTagsEntity(EndUserTagsRequest endUserTagsRequest) {
        if ( endUserTagsRequest == null ) {
            return null;
        }

        EndUserTagsEntity endUserTagsEntity = new EndUserTagsEntity();

        endUserTagsEntity.setTagId( endUserTagsRequest.getTagId() );
        endUserTagsEntity.setTagName( endUserTagsRequest.getTagName() );

        return endUserTagsEntity;
    }

    protected Set<EndUserTagsEntity> endUserTagsRequestListToEndUserTagsEntitySet(List<EndUserTagsRequest> list) {
        if ( list == null ) {
            return null;
        }

        Set<EndUserTagsEntity> set = new LinkedHashSet<EndUserTagsEntity>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( EndUserTagsRequest endUserTagsRequest : list ) {
            set.add( endUserTagsRequestToEndUserTagsEntity( endUserTagsRequest ) );
        }

        return set;
    }

    protected AddressDto addressToAddressDto(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressDto addressDto = new AddressDto();

        addressDto.setAddressId( address.getAddressId() );
        addressDto.setDetails( address.getDetails() );
        addressDto.setCity( address.getCity() );
        addressDto.setState( address.getState() );
        addressDto.setPincode( address.getPincode() );

        return addressDto;
    }

    protected List<PacketClassificationResponse> packetClassificationSetToPacketClassificationResponseList(Set<PacketClassification> set) {
        if ( set == null ) {
            return null;
        }

        List<PacketClassificationResponse> list = new ArrayList<PacketClassificationResponse>( set.size() );
        for ( PacketClassification packetClassification : set ) {
            list.add( packetClassificationMapper.toResponse( packetClassification ) );
        }

        return list;
    }

    protected EndUserTagsResponse endUserTagsEntityToEndUserTagsResponse(EndUserTagsEntity endUserTagsEntity) {
        if ( endUserTagsEntity == null ) {
            return null;
        }

        EndUserTagsResponse endUserTagsResponse = new EndUserTagsResponse();

        endUserTagsResponse.setTagId( endUserTagsEntity.getTagId() );
        endUserTagsResponse.setTagName( endUserTagsEntity.getTagName() );
        if ( endUserTagsEntity.getCreatedOn() != null ) {
            endUserTagsResponse.setCreatedOn( new SimpleDateFormat().format( endUserTagsEntity.getCreatedOn() ) );
        }
        if ( endUserTagsEntity.getUpdatedOn() != null ) {
            endUserTagsResponse.setUpdatedOn( new SimpleDateFormat().format( endUserTagsEntity.getUpdatedOn() ) );
        }

        return endUserTagsResponse;
    }

    protected List<EndUserTagsResponse> endUserTagsEntitySetToEndUserTagsResponseList(Set<EndUserTagsEntity> set) {
        if ( set == null ) {
            return null;
        }

        List<EndUserTagsResponse> list = new ArrayList<EndUserTagsResponse>( set.size() );
        for ( EndUserTagsEntity endUserTagsEntity : set ) {
            list.add( endUserTagsEntityToEndUserTagsResponse( endUserTagsEntity ) );
        }

        return list;
    }
}
