package com.steel.product.application.mapper;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.dto.party.PartyResponse;
import com.steel.product.application.entity.Party;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {AddressMapper.class,PacketClassificationMapper.class})
public interface PartyMapper {

    Party toEntity(PartyDto partyDto);
    PartyResponse toResponse(Party party);
    List<PartyResponse> toResponseList(List<Party> party);
}
