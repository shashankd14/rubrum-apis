package com.steel.product.application.mapper;

import com.steel.product.application.dto.party.PartyDto;
import com.steel.product.application.entity.Party;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {AddressMapper.class})
public interface PartyMapper {

    Party toEntity(PartyDto partyDto);
}
